package com.wintersky.windyreader;

import com.wintersky.windyreader.data.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ZxzwComLua {

    @Test
    public void getBook() throws Exception {
        String url = "http://zxzw.com/26133/";
        Document doc = Jsoup.connect(url).timeout(3000).get();

        String BT = doc.select("div.text.t_c").get(0).select("a").get(0).ownText();

        System.out.println("BU\n" + url);
        System.out.println("BT\n" + BT);
        System.out.println("CU\n" + url);
    }

    @Test
    public void getCatalog() throws Exception {
        List<Chapter> list = new ArrayList<>();
        long s = System.currentTimeMillis();
        Document doc = Jsoup.connect("http://zxzw.com/164588/").timeout(3000).get();
        long e = System.currentTimeMillis();
        Element divCList = doc.select("div.chapters").get(0);
        Elements aCList = divCList.select("a");
        for (int i = 0; i < aCList.size(); i++) {
            Chapter chapter = new Chapter();
            Element aC = aCList.get(i);
            chapter.setIndex(i + 1);
            chapter.setTitle(aC.attr("title"));
            chapter.setUrl(aC.absUrl("href"));
            list.add(chapter);
        }
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getIndex()).append(" ")
                    .append(c.getTitle()).append(" ")
                    .append(c.getUrl()).append("\n");
        }
        System.out.println(sb.toString());
        System.out.println(e - s);
    }

    @Test
    public void getChapter() throws Exception {
        long s = System.currentTimeMillis();
        Document doc = Jsoup.connect("http://zxzw.com/164588/14192209/").timeout(3000).get();
        String CT = doc.select("div.text.t_c").get(0).child(0).ownText();
        String CC = doc.select("div#content").get(0).html();
        CC = CC.substring(CC.lastIndexOf("</div>") + 8, CC.indexOf("<input") - 1);
        CC = CC.replaceAll("<br>", "");
        String CP = doc.select("a#prevLink").get(0).absUrl("href");
        String CN = doc.select("a#nextLink").get(0).absUrl("href");
        String CS = doc.select("a#home").get(0).absUrl("href");
        long e = System.currentTimeMillis();

        System.out.println("CT\n" + CT);
        System.out.println("CC\n" + CC);
        System.out.println("CP\n" + CP);
        System.out.println("CN\n" + CN);
        System.out.println("CS\n" + CS);
        System.out.println(e - s);
    }
}
