# 简单了解Elasticsearch
```text
1.什么是Elasticsearch(简称ES):
    Elasticsearch VS Solr: https://www.cnblogs.com/jajian/p/9801154.html
    [1]基于Apache Lucene构建的开源搜索引擎,提供一个分布式多用户能力的全文搜索引擎;
    [2]用Java编写的,提供简单易用的RESTFul API,当前流行的企业级搜索引擎;
    [3]轻松的横向扩展,可支持PB级的结构化或非结构化数据处理;
    [4]可以准实时地快速存储、搜索、分析海量的数据(用于云计算中，能够达到实时搜索)
2.应用场景:
    [1]海量数据分析引擎(聚合搜索)
    [2]站内搜索引擎
    [3]数据仓库
3.Elasticsearch有几个核心概念:
    [1]接近实时(NRT): 从索引一个文档直到这个文档能够被搜索到有一个轻微的延迟(通常是1秒)
    [2]集群(cluster):
        (1)一个集群就是由一个或多个节点组织在一起,它们共同持有你整个的数据,并一起提供索引和搜索功能;
        (2)一个集群由一个唯一的名字标识,这个名字默认就是Elasticsearch;
            1)这个名字是重要的,因为一个节点只能通过指定某个集群的名字,来加入这个集群;
            2)在产品环境中显式地设定这个名字是一个好习惯,但是使用默认值来进行测试/开发也是不错的;
    [3]节点(node):
        (1)每个节点是集群中的一个服务器,作为集群的一部分,它存储数据并参与集群的索引和搜索功能;
        (2)和集群类似,每个节点由一个名字来标识的;
            1)默认情况下,这个名字是一个随机的漫威漫画角色的名字,该名字会在启动的时候赋予节点;
            2)节点名字对于管理工作来说挺重要的,因为在这个管理过程中,会去确定网络中的哪些服务器对应于Elasticsearch集群中的哪些节点;
        (3)一个节点可以通过配置集群名称的方式来加入一个指定的集群;
            默认情况下,每个节点都会被安排加入到一个叫做Elasticsearch的集群中;
        (4)在一个集群里,可以拥有任意多个节点;
            如果当前网络中没有运行任何Elasticsearch节点,这时启动一个节点,会默认创建并加入一个叫做Elasticsearch的集群;
    [4]索引(index):
        (1)一个索引就是一个拥有几分相似特征的文档的集合;
        (2)一个索引由一个名字来标识(必须全部是小写字母的),并且当我们要对对应于这个索引中的文档进行索引,搜索,更新和删除的时,都要使用到这个名字;
        (3)索引类似于关系型数据库中Database的概念;
    [5]类型(type):
        (1)在一个索引中,可以定义一种或多种类型;一个类型是你的索引的一个逻辑上的分类/分区;通常,会为具有一组共同字段的文档定义一个类型;
        (2)类型类似于关系型数据库中Table的概念;
    [6]文档(document):
        (1)一个文档是一个可被索引的基础信息单元;
            文档以JSON(Javascript Object Notation)格式来表示,而JSON是一个到处存在的互联网数据交互格式;
        (2)一个文档,物理上存在于一个索引之中,但文档必须被索引赋予一个类型type;
        (3)文档类似于关系型数据库中Record的概念;实际上一个文档除了用户定义的数据外,还包括**_index**、_type和**_id**字段;
    [7]分片和复制(shards & replicas):
        (1)一个索引可以存储超出单个结点硬件限制的大量数据;
        (2)为了解决索引存储超单节点硬件限制问题,Elasticsearch提供了将索引划分成多份的能力,这些份就叫做分片;
            1)当你创建一个索引的时候,可指定想要的分片的数量;
            2)每个分片本身也是一个功能完善并且独立的“索引”,这个“索引”可以被放置到集群中的任何节点上;
            3)分片之所以重要,主要有两方面的原因:
                允许你水平分割/扩展你的内容容量;
                允许你在分片(潜在地,位于多个节点上)之上进行分布式的、并行的操作,进而提高性能/吞吐量;
        (3)某个分片/节点随时可能处于离线状态,因此需要一个故障转移机制;为此Elasticsearch允许创建分片的一份或多份拷贝,这些拷贝叫做复制分片,或者直接叫复制;
            1)复制之所以重要,主要有两方面的原因:
                在分片/节点失败的情况下,提供了高可用性;因为这个原因,注意到复制分片从不与原/主要(original/primary)分片置于同一节点上是非常重要的;
                扩展你的搜索量/吞吐量,因为搜索可以在所有的复制上并行运行;
        (4)每个索引可以被分成多个分片;一个索引也可以被复制0次(意思是没有复制)或多次;
            1)一旦复制了,每个索引就有了主分片(作为复制源的原来的分片)和复制分片(主分片的拷贝)之别;
            2)分片和复制的数量可以在索引创建的时候指定;在索引创建之后,可在任何时候动态地改变复制数量,但是不能改变分片的数量;
        (5)默认情况下,Elasticsearch中的每个索引被分片5个主分片和1个复制;
            1)即集群中至少有两个节点,索引将会有5个主分片和另外5个复制分片(1个完全拷贝),这样的话每个索引总共就有10个分片;
            2)一个索引的多个分片可存放在集群中的一台主机上,也可以存放在多台主机上,这取决于你的集群机器数量;
                主分片和复制分片的具体位置是由ES内在的策略所决定的;
    [8]数据结构及对比
        (1)Elasticsearch的结构:
            1)索引: 含有相同属性的文档集合;
            2)类型: 索引可以定义一个或多个类型,文档必须属于一个类型;
            3)文档: 可以被索引的基础数据单位;
            4)分片: 每个索引都有多个分片,每个分片都是Lucene索引;
            5)备份: 拷贝一份分片就完成分片的备份;
        (2)Mysql和Elasticsearch对应关系:
            Mysql	        Elasticsearch
            Database	    Index
            Table	        Type
            Row	            Document
            Column	        Field
            Schema	        Mapping
            Index	        Everything is indexed
            SQL	            Query DSL
```
# 安装本地Elasticsearch
```text
1.本地Elasticsearch单机搭建:
    [1]首先要安装JDK1.8的环境及以上版本都行,不能低于1.8;安装Windows本地版,去Elasticsearch官网下载zip压缩包并解压即可;
        (1)目录说明:
            1)config: 配置文件
            2)modules: 模块存放目录
            3)bin: 脚本
            4)lib: 第三方库
            5)plugins: 第三方插件
        (2)直接运行bin下的elasticsearch.bat这个文件即可启动,然后访问本机的127.0.0.1:9200即可;关闭窗口就是关闭服务;
2.安装本地Elasticsearch集群(分布式):
    [1]安装说明: 三个节点,一个Master,两个Slave,名称要相同,9500端口为Master节点,其余两个为Slave节点:
        集群名称	    IP-端口
        myEsCluster	    127.0.0.1:9500
        myEsCluster	    127.0.0.1:9600
        myEsCluster	    127.0.0.1:9700
    [2]ES安装包解压出三份ES,修改每个Elasticsearch安装目录下的config/elasticsearch.yml配置文件:
        (1)Master配置说明:
			# 设置支持Elasticsearch-Head
			http.cors.enabled: true
			http.cors.allow-origin: "*"
			# 设置集群Master配置信息
			cluster.name: myEsCluster
			# 节点的名字,一般为Master或者Slave
			node.name: master
			# 节点是否为Master,设置为true的话,说明此节点为Master节点
			node.master: true
			# 设置网络,如果是本机的话就是127.0.0.1,其他服务器配置对应的IP地址即可(0.0.0.0支持外网访问)
			network.host: 127.0.0.1
			# 设置对外服务的Http端口,默认为 9200,可以修改默认设置
			http.port: 9500
			# 设置节点间交互的TCP端口,默认是9300
			transport.tcp.port: 9300
			# 手动指定可以成为Master的所有节点的Name或者IP,这些配置将会在第一次选举中进行计算
			cluster.initial_master_nodes: ["127.0.0.1"]
		(2)Slave配置说明:
		    1)Slave1:
				# 设置集群Slave配置信息
				cluster.name: myEsCluster
				# 节点的名字,一般为Master或者Slave
				node.name: slave1
				# 节点是否为Master,设置为true的话,说明此节点为master节点
				node.master: false
				# 设置对外服务的Http端口,默认为 9200,可以修改默认设置
				http.port: 9600
				# 设置网络,如果是本机的话就是127.0.0.1,其他服务器配置对应的IP地址即可(0.0.0.0支持外网访问)
				network.host: 127.0.0.1
				# 集群发现
				discovery.seed_hosts: ["127.0.0.1:9300"]
			2)Slave2:
				# 设置集群Slave配置信息
				cluster.name: myEsCluster
				# 节点的名字,一般为Master或者Slave
				node.name: slave2
				# 节点是否为Master,设置为true的话,说明此节点为master节点
				node.master: false
				# 设置对外服务的Http端口,默认为 9200,可以修改默认设置
				http.port: 9700
				# 设置网络,如果是本机的话就是127.0.0.1,其他服务器配置对应的IP地址即可(0.0.0.0支持外网访问)
				network.host: 127.0.0.1
				# 集群发现
				discovery.seed_hosts: ["127.0.0.1:9300"]
	[3]配置后完成后,启动一个Master,两个Slave;访问http://localhost:9500/_cat/nodes?v;
```
# 安装本地分词插件(IK和拼音)
```text
1.安装本地Elasticsearch的IK分词插件:
    [1]IK分词器(需要版本对应)下载地址: https://github.com/medcl/elasticsearch-analysis-ik/releases
    [2]解压分词插件的压缩包到Elasticsearch目录的plugins目录下:
        例如: elasticsearch-7.6.0\plugins\elasticsearch-analysis-ik-7.6.0
    [3]重启Elasticsearch,在控制台上看到分词插件加载的日志,则分词安装成功;
        loaded plugin [analysis-ik]
    [4]测试:
		POST /_analyze
		{
		    "text":"中华人民共和国国徽",
		    "analyzer":"ik_smart"
		} 
		POST /_analyze
		{
			"text":"中华人民共和国国徽",
			"analyzer":"ik_max_word"
		}
2.安装本地Elasticsearch的拼音分词插件:
    [1]拼音分词器(需要版本对应)下载地址:https://github.com/medcl/elasticsearch-analysis-pinyin/releases
    [2]解压分词插件的压缩包到Elasticsearch目录的plugins目录下:
        elasticsearch-7.6.0\plugins\elasticsearch-analysis-pinyin-7.6.0
    [3]重启Elasticsearch,在控制台上看到分词插件加载的日志,则分词安装成功;
        loaded plugin [analysis-pinyin]
    [4]测试:
        POST /_analyze
        {
          "text":"中华人民共和国国徽",
          "analyzer":"pinyin"
        }
```
# SpringBoot整合Elasticsearch
```text
1.SpringBoot整合Elasticsearch的方式(TransportClient、Data-ES、Elasticsearch SQL、REST Client):
    [1]TransportClient:
        TransportClient即将弃用,所以这种方式不考虑;
    [2]Data-ES:
        (1)Spring提供的封装的方式,好像底层也是基于TransportClient,Elasticsearch7.0后的版本不怎么支持;
        (2)Spring Data Elasticsearch版本与Elasticsearch官网版本差太多;
    [3]Elasticsearch SQL:
        将Elasticsearch的Query DSL用SQL转换查询,早期有一个第三方的插件Elasticsearch-SQL,后来随着官方也开始做这方面,该插件不怎么更新了;
        参考: https://www.cnblogs.com/jajian/p/10053504.html
    [4]REST Client:
        (1)官方推荐使用,分为两个Low Level REST Client和High Level REST Client;
        (2)Low Level REST Client是早期出的API比较简陋了,还需要自己去拼写Query DSL;
        (3)High Level REST Client使用起来更好用,更符合面向对象的感觉;
2.创建index和mapping:
    [1]创建Index,拼音分词过滤:
		PUT /book
		{
			"settings": {
				"analysis": {
					"analyzer": {
						"pinyin_analyzer": {
							"tokenizer": "my_pinyin"
						}
					},
					"tokenizer": {
						"my_pinyin": {
							"type": "pinyin",
							"keep_separate_first_letter": false,
							"keep_full_pinyin": true,
							"keep_original": true,
							"limit_first_letter_length": 16,
							"lowercase": true,
							"remove_duplicated_term": true
						}
					}
				}
			}
		}
		返回:
		{
			"acknowledged": true,
			"shards_acknowledged": true,
			"index": "book"
		}
	[2]创建Mapping,属性使用过滤,name开启拼音分词,content开启IK分词,describe开启拼音加IK分词:
		POST /book/_mapping
		{
			"properties": {
				"name": {
					"type": "keyword",
					"fields": {
						"pinyin": {
							"type": "text",
							"store": false,
							"term_vector": "with_offsets",
							"analyzer": "pinyin_analyzer",
							"boost": 10
						}
					}
				},
				"content": {
					"type": "text",
					"analyzer": "ik_max_word",
					"search_analyzer": "ik_smart"
				},
				"describe": {
					"type": "text",
					"analyzer": "ik_max_word",
					"search_analyzer": "ik_smart",
					"fields": {
						"pinyin": {
							"type": "text",
							"store": false,
							"term_vector": "with_offsets",
							"analyzer": "pinyin_analyzer",
							"boost": 10
						}
					}
				},
				"id": {
					"type": "long"
				}
			}
		}
		返回:
		{
            "acknowledged": true
        }
2.创建一个SpringBoot 2.2.6的Maven项目,添加如下REST Client依赖和其他用到的依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <!-- Java Low Level REST Client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>7.6.0</version>
    </dependency>
    <!-- Java High Level REST Client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.6.0</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.68</version>
    </dependency>
    <!-- Commons-Lang3工具包 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.10</version>
    </dependency>
3.配置文件:
	server:
	port: 8080
	spring:
	application:
		name: springboot-elasticsearch
	thymeleaf:
		# 开发时关闭缓存不然没法看到实时页面
		cache: off
		# 启用不严格检查
		mode: LEGACYHTML5
	# Elasticsearch配置
	elasticsearch:
	hostname: localhost
	port: 9200
4.编写配置类:
 	@Configuration
	public class RestClientConfig {
		@Value("${elasticsearch.hostname}")
		private String hostname;
		@Value("${elasticsearch.port}")
		private int port;
		/**
		* LowLevelRestConfig
		*
		* @return org.elasticsearch.client.RestClient
		*/
		@Bean
		public RestClient restClient() {
			// 如果有多个从节点可以持续在内部new多个HttpHost,参数1是IP,参数2是端口,参数3是通信协议
			RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(hostname, port, "http"));
			// 设置Header编码
			Header[] defaultHeaders = {new BasicHeader("content-type", "application/json")};
			clientBuilder.setDefaultHeaders(defaultHeaders);
			// 添加其他配置,这些配置都是可选的
			// 详情配置可看https://blog.csdn.net/jacksonary/article/details/82729556
			return clientBuilder.build();
		}
		/**
		* HighLevelRestConfig
		*
		* @return org.elasticsearch.client.RestClient
		*/
		@Bean
		public RestHighLevelClient restHighLevelClient() {
			// 如果有多个从节点可以持续在内部new多个HttpHost,参数1是IP,参数2是端口,参数3是通信协议
			return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
		}
	}
4.其余代码见demo;
```