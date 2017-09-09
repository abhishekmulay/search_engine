package hw3.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hw1.restclient.RestCallHandler;

import java.util.*;

/**
 * Created by Abhishek Mulay on 6/26/17.
 */


public class DocumentModel {
    String docno;
    Map<String, String> HTTPheader;
    String title;
    String text;
    String html_Source;
    List<String> in_links;
    List<String> out_links;

    String author;

    final int depth;
    final String url;

    public DocumentModel() {
        this.docno = null;
        this.HTTPheader = null;
        this.title = null;
        this.text = null;
        this.html_Source = null;
        this.in_links = null;
        this.out_links = null;
        this.author = null;
        this.depth = -1;
        this.url = null;
    }

    public void setIn_links(List<String> in_links) {
        this.in_links = in_links;
    }

    public DocumentModel(String docno, Map<String, String> HTTPheader, String title, String text, String html_Source, List<String> in_links, List<String> out_links, String author, int depth, String url) {
        this.docno = docno;
        this.HTTPheader = HTTPheader;
        this.title = title;
        this.text = text;
        this.html_Source = html_Source;
        this.in_links = in_links;
        this.out_links = out_links;
        this.author = author;
        this.depth = depth;
        this.url = url;

    }

    public String getDocno() {
        return docno;
    }

    public Map<String, String> getHTTPheader() {
        return HTTPheader;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getHtml_Source() {
        return html_Source;
    }

    public List<String> getIn_links() {
        return in_links;
    }

    public List<String> getOut_links() {
        return out_links;
    }

    public String getAuthor() {
        return author;
    }

    public int getDepth() {
        return depth;
    }

    public String getUrl() {
        return url;
    }


    public void setDocno(String docno) {
        this.docno = docno;
    }

    public void setHTTPheader(Map<String, String> HTTPheader) {
        this.HTTPheader = HTTPheader;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setHtml_Source(String html_Source) {
        this.html_Source = html_Source;
    }

    public void setOut_links(List<String> out_links) {
        this.out_links = out_links;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void attachedInlinks(Set<String> in_linksSet) {
        if (in_linksSet == null || in_linksSet.isEmpty()) {
            if (this.in_links !=null){
                this.in_links.addAll(Collections.emptyList());
            }
        } else {
            this.in_links = new ArrayList<>();
            this.in_links.addAll(in_linksSet);
        }
    }
}

