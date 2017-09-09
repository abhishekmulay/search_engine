package hw1.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by Abhishek Mulay on 5/10/17.
 */
public class HW1Model {
    // required fields
    private String docno;
    private String text;

    // extra fields
    @JsonIgnore
    private String fileId;
    @JsonIgnore
    private String first;
    @JsonIgnore
    private String second;
    @JsonIgnore
    private String dateline;

    private List<String> heads;
    @JsonIgnore
    private List<String> bylines;

    private int docLength;

    public HW1Model(String docId, String text, String fileId, String first, String second, String dateline, List<String> heads, List<String> bylines) {
        this.docno = docId;
        this.text = text;

        this.fileId = fileId;
        this.first = first;
        this.second = second;
        this.heads = heads;
        this.bylines = bylines;
        this.dateline = dateline;
        this.docLength = text.split(" ").length;

    }

    public String getDocno() {
        return docno;
    }

    public void setDocno(String docno) {
        this.docno = docno;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public List<String> getHeads() {
        return heads;
    }

    public void setHeads(List<String> heads) {
        this.heads = heads;
    }

    public List<String> getBylines() {
        return bylines;
    }

    public void setBylines(List<String> bylines) {
        this.bylines = bylines;
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    @Override
    public String toString() {
        return "HW1Model{" +
                "docno='" + docno + '\'' +
                ", text='" + text + '\'' +
                ", fileId='" + fileId + '\'' +
                ", first='" + first + '\'' +
                ", second='" + second + '\'' +
                ", dateline='" + dateline + '\'' +
                ", heads=" + heads +
                ", bylines=" + bylines +
                '}';
    }
}
