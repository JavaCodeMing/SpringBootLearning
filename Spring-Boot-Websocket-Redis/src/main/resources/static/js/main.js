'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let username = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        /*以端点"/ws"创建客户端*/
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // 订阅"/topic/public",收到消息则调用onMessageReceived函数
    stompClient.subscribe('/topic/public', onMessageReceived);
    /*将消息发送到消息代理,再通过消息代理路由到控制器*/
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );
    connectingElement.classList.add('hidden');
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    /*获取id为message的输入框的值*/
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        /*将消息发送到消息代理,再通过消息代理路由到控制器*/
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

/*该函数用来显示用户加入、离开和聊天消息*/
function onMessageReceived(payload) {
    /*获取服务器发送来的消息*/
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');
    if(message.type === 'JOIN') {           /*当消息类型为"JOIN"时,使用用户加入标识替换消息内容*/
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {  /*当消息类型为"LEAVE"时,使用用户离开标识替换消息内容*/
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {                               /*其他的消息类型则,添加发送消息的用户*/
        messageElement.classList.add('chat-message');
        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    /*添加消息内容来展示*/
    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
}

/*事件监听: 输入用户名提交时触发connect函数*/
usernameForm.addEventListener('submit', connect, true);
/*事件监听: 输入消息提交时触发sendMessage函数*/
messageForm.addEventListener('submit', sendMessage, true);