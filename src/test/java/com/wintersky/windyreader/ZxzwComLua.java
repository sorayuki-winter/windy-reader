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
    public void getChapterList() throws Exception {
        List<Chapter> list = new ArrayList<>();

        Document doc = Jsoup.connect("http://zxzw.com/164588/").timeout(3000).get();
        Element divCList = doc.select("div.chapters").get(0);
        Elements aCList = divCList.select("a");
        for (int i = 0; i < aCList.size(); i++) {
            Chapter chapter = new Chapter();
            Element aC = aCList.get(i);
            chapter.id = i + 1;
            chapter.title = (aC.attr("title"));
            chapter.url = (aC.absUrl("href"));
            list.add(chapter);
        }

        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.id).append(" ")
                    .append(c.title).append(" ")
                    .append(c.url).append("\n");
        }
        System.out.println(sb.toString());
    }

    @Test
    public void getChapter() throws Exception {
        Document doc = Jsoup.connect("http://zxzw.com/164588/14192209/").timeout(3000).get();
        String CT = doc.select("div.text.t_c").get(0).child(0).ownText();
        String CC = doc.select("div#content").get(0).html();
        CC = CC.substring(CC.lastIndexOf("</div>") + 8, CC.indexOf("<input") - 1);
        CC = CC.replaceAll("<br>", "");
        String CP = doc.select("a#prevLink").get(0).absUrl("href");
        String CN = doc.select("a#nextLink").get(0).absUrl("href");
        String CS = doc.select("a#home").get(0).absUrl("href");

        System.out.println("CT\n" + CT);
        System.out.println("CC\n" + CC);
        System.out.println("CP\n" + CP);
        System.out.println("CN\n" + CN);
        System.out.println("CS\n" + CS);
    }
}
