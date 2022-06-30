package postgresqlConnect;

import com.vdurmont.emoji.EmojiParser;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbConnect {
    public static void main(String[] args) throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost/smartchian";
        String user = "test01";
        String password = "test01"; //password 입력

        try
        {
            Connection connect = null;
            connect = DriverManager.getConnection(dbUrl, user, password);
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM 금융_20220101_20220331");

            //날짜포멧
            SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat date1 = new SimpleDateFormat("yyyy-MM-dd");

            JSONArray arr = new JSONArray();

            while (rs.next()){
                String content = rs.getString("content");
                String title = rs.getString("title");
                String cutTitle = title;
                //DB에서 데이터 불러오기
                String dbDate = rs.getString("write_date");
                //원래 String이었던거 date로 변환시켜주고
                Date oriDate = date.parse(dbDate);
                //원하는 포멧으로 바꿔서 String으로 저장
                String chgDate = date1.format(oriDate);
                System.out.println(chgDate);
//                String contact2 = rs.getString("contact");
                String contact2 = "";
                if (rs.getString("contact") != null) {
                    contact2 = rs.getString("contact");
                }

                //이모티콘 제거
                String delEmoji = EmojiParser.removeAllEmojis(content);
                
                //타이틀60자넘으면 60자로자르고 뒤에 ...붙이기
                if(title.length()>60) {
                    cutTitle = title.substring(0, 60) + "...";
                }
//                System.out.println(delEmoji);

                JSONObject obj = new JSONObject();
                obj.put("seq", rs.getString("seq"));
                obj.put("url", rs.getString("url"));
                obj.put("channel", rs.getString("channel"));
                obj.put("title", cutTitle);
                obj.put("content", delEmoji.replaceAll("\\,", "^").replaceAll("[\\t]", ""));
                obj.put("write_date", chgDate);
                obj.put("write_time", rs.getString("write_time"));
                obj.put("writer_name", rs.getString("writer_name"));
                obj.put("writer_account", rs.getString("writer_account"));
                obj.put("contact", contact2);
                obj.put("keyword_groups", rs.getString("keyword_groups"));
                obj.put("keywords", rs.getString("keywords"));

                arr.put(obj);
//                String seq = rs.getString("seq");
//                String url = rs.getString("url");
//                String channel = rs.getString("channel");
//                String title = rs.getString("title");
//                String content = rs.getString("content");
//                String writeDate = rs.getString("write_date");
//                String writeTime = rs.getString("write_time");
//                String writeName = rs.getString("write_name");
//                String writeAccount = rs.getString("write_account");
//                String contact = rs.getString("contact");
//                String keywordGroups = rs.getString("keyword_groups");
//                String keywords = rs.getString("keywords");


            }
            rs.close();
            stmt.close();
            connect.close();

            //Json to csv
            String csv = CDL.toString(arr);
//            String testJson = arr.toString();
            //파일저장경로
            File jsonFile = new File("C:\\Users\\e2on\\Desktop\\jsontest\\wpdltmstest.csv");
            writeStringToFile(csv, jsonFile);

            if(connect != null) {
                System.out.println("Connection successful!!");
            }
            else {
                throw new SQLException("no connection...");
            }
        } catch (SQLException ex) {
            throw ex;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void writeStringToFile(String str, File file) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
    }
}
