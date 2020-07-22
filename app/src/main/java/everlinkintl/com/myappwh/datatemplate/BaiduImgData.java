package everlinkintl.com.myappwh.datatemplate;

import java.util.List;

public class BaiduImgData {
    private String log_id;
    private String words_result_num;
    private List<WordsResultData> words_result;

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(String words_result_num) {
        this.words_result_num = words_result_num;
    }

    public List<WordsResultData> getWords_result() {
        return words_result;
    }

    public void setWords_result(List<WordsResultData> words_result) {
        this.words_result = words_result;
    }
}
