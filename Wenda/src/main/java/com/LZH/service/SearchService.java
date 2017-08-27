package com.LZH.service;

import com.LZH.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/5/9.
 */
/*
@Service
public class SearchService {

    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/collection1";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    public List<Question> searchQuestion(String keyword, int offset, int limit, String hlPre, String hlPos) throws Exception{
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(limit);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("h1.f1"+QUESTION_TITLE_FIELD+","+QUESTION_CONTENT_FIELD);
        QueryResponse response = client.query(query);
        for (Map.Entry<String,Map<String,List<String>>> entry : response.getHighlighting().entrySet()){
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if(entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if(contentList.size()>0){
                    q.setContent(contentList.get(0));
                }
            }
            if(entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titletList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if(titletList.size()>0){
                    q.setTitle(titletList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }

    public boolean indexQuestion(int qid, String title, String content) throws Exception{
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(QUESTION_TITLE_FIELD,title);
        doc.setField(QUESTION_CONTENT_FIELD,content);
        UpdateResponse updateResponse = client.add(doc,1000);
        return updateResponse!=null && updateResponse.getStatus()!=0;
    }
}
*/
