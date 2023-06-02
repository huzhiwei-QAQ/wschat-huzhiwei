package cn.molu.app.test;

import cn.molu.app.pojo.User;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;

import javax.naming.directory.SearchResult;
import java.util.Date;

public class ElasticSearch {


    /**
     * 创建索引
     * @throws Exception
     */

    public  void createIndex() throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));
        //创建索引
        CreateIndexRequest user = new CreateIndexRequest("student");
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(user, RequestOptions.DEFAULT);
        //创建索引状态
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println("创建索引状态:"+acknowledged);
        restHighLevelClient.close();
    }

    /**
     * 获取索引
     * @throws Exception
     */

    public  void getIndex()throws  Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        GetIndexRequest user = new GetIndexRequest("user");
        GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(user, RequestOptions.DEFAULT);
        System.out.println(getIndexResponse);
        System.out.println(getIndexResponse.getSettings());
        System.out.println(getIndexResponse.getAliases());
        System.out.println(getIndexResponse.getMappings());
        restHighLevelClient.close();
    }


    /**
     * 删除索引
     * @param args
     * @throws Exception
     */

    public void deleIndex() throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        DeleteIndexRequest user = new DeleteIndexRequest("user");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(user, RequestOptions.DEFAULT);
        System.out.println("删除的状态："+delete.isAcknowledged());
        restHighLevelClient.close();
    }

    /**
     * 添加文档
     * @param args
     * @throws Exception
     */
    public void createDoc() throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        IndexRequest indexRequest=new IndexRequest();
        indexRequest.index("user").id("001");

        User user=new User();
        user.setId(1);
        user.setUserCode("1212121212");
        user.setPhone("18408227651");
        user.setUsername("胡志伟");
        user.setCreated(new Date());

        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("_index：" + index.getIndex());
        System.out.println("_id：" + index.getId());
        System.out.println("_result：" + index.getResult());
        restHighLevelClient.close();
    }

    /**
     * 查询文档
     * @param args
     * @throws Exception
     */
      public void getDoc()throws  Exception{
          RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                  RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

          GetRequest getRequest=new GetRequest();
          getRequest.index("user").id("001");
          GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

          System.out.println("_index:" + getResponse.getIndex());
          System.out.println("_type:" + getResponse.getType());
          System.out.println("_id:" + getResponse.getId());
          System.out.println("source:" + getResponse.getSourceAsString());
          restHighLevelClient.close();

      }

    /**
     * 修改文档
     * @param args
     * @throws Exception
     */
    public void updateDoc()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        UpdateRequest updateRequest=new UpdateRequest();
        updateRequest.index("user").id("001");
        updateRequest.doc(XContentType.JSON,"username","张三");
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println("_index：" + update.getIndex());
        System.out.println("_id：" + update.getId());
        System.out.println("_result：" + update.getResult());
        restHighLevelClient.close();
    }

    /**
     * 删除文档
     * @param args
     * @throws Exception
     */

    public void deleDoc()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        DeleteRequest deleteRequest=new DeleteRequest();
        deleteRequest.index("user").id("001");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(delete.toString());
        restHighLevelClient.close();
    }

    /**
     * 批量新增
     * @param args
     * @throws Exception
     */
    public void batchCreateDoc()throws  Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        User user=new User();
        user.setId(1);
        user.setUserCode("1212121212");
        user.setPhone("18408227651");
        user.setUsername("胡志伟");
        user.setCreated(new Date());

        User user1=new User();
        user1.setId(2);
        user1.setUserCode("12121212121");
        user1.setPhone("18408227652");
        user1.setUsername("张三");
        user1.setCreated(new Date());

        User user2=new User();
        user2.setId(3);
        user2.setUserCode("12121212122");
        user2.setPhone("18408227653");
        user2.setUsername("李四");
        user2.setCreated(new Date());

        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.add(new IndexRequest().index("user").id("1001").source(JSON.toJSONString(user),XContentType.JSON));
        bulkRequest.add(new IndexRequest().index("user").id("1002").source(JSON.toJSONString(user1),XContentType.JSON));
        bulkRequest.add(new IndexRequest().index("user").id("1003").source(JSON.toJSONString(user2),XContentType.JSON));
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println("took：" + bulk.getTook());
        System.out.println("items：" + bulk.getItems());
        restHighLevelClient.close();
    }


    /**
     * 请求体查询
     * @param args
     * @throws Exception
     */
    public void RequestBodyQuery()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        SearchRequest searchRequest=new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took：" + search.getTook());
        System.out.println("是否超时：" + search.isTimedOut());
        System.out.println("TotalHits：" + hits.getTotalHits());
        System.out.println("MaxScore：" + hits.getMaxScore());
        for (SearchHit hit:hits) {
            System.out.println(hit.getSourceAsString());
        }
        restHighLevelClient.close();
    }


    /**
     * term查询
     * @param args
     * @throws Exception
     */
    public void TremQuery()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        SearchRequest searchRequest=new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("phone","18408227651"));
//        //如果要使用name中文查询，是查不出来的，要改成name.keyword 如果是数字,可以不用加.keyword
//        searchSourceBuilder.query(QueryBuilders.termQuery("username.keyword","胡志伟"));
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took：" + search.getTook());
        System.out.println("是否超时：" + search.isTimedOut());
        System.out.println("TotalHits：" + hits.getTotalHits());
        System.out.println("MaxScore：" + hits.getMaxScore());
        for (SearchHit hit:hits) {
            System.out.println(hit.getSourceAsString());
        }
        restHighLevelClient.close();
    }


    public void PageQuery()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        SearchRequest searchRequest=new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(2);

        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took：" + search.getTook());
        System.out.println("是否超时：" + search.isTimedOut());
        System.out.println("TotalHits：" + hits.getTotalHits());
        System.out.println("MaxScore：" + hits.getMaxScore());
        for (SearchHit hit:hits) {
            System.out.println(hit.getSourceAsString());
        }
        restHighLevelClient.close();
    }

    public void filterFiledQuery()throws Exception{
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("43.139.56.154",9200,"http")));

        SearchRequest searchRequest=new SearchRequest();
        searchRequest.indices("user");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        String [] excludes={};
        String [] includes={"userCode"};
        searchSourceBuilder.fetchSource(excludes,includes);
//        searchSourceBuilder.fetchField("userCode");
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println("took：" + search.getTook());
        System.out.println("是否超时：" + search.isTimedOut());
        System.out.println("TotalHits：" + hits.getTotalHits());
        System.out.println("MaxScore：" + hits.getMaxScore());
        for (SearchHit hit:hits) {
            System.out.println(hit.getSourceAsString());
        }
        restHighLevelClient.close();
    }



    public static void main(String[] args) throws Exception {

        ElasticSearch elasticSearch=new ElasticSearch();
//        elasticSearch.createIndex();
//        elasticSearch.getIndex();
//        elasticSearch.deleIndex();
//        elasticSearch.createDoc();
//        elasticSearch.getDoc();
//        elasticSearch.deleDoc();
//        elasticSearch.batchCreateDoc();
//        elasticSearch.RequestBodyQuery();
//        elasticSearch.TremQuery();
//        elasticSearch.PageQuery();
          elasticSearch.filterFiledQuery();
    }
}
