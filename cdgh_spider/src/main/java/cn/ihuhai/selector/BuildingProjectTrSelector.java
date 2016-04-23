package cn.ihuhai.selector;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huhai on 2016/4/21.
 */
public class BuildingProjectTrSelector implements Selector {
    private Xsoup xsoup = new Xsoup();

    @Override
    public String select(String s) {

        return s;
    }

    @Override
    public List<String> selectList(String s) {
        List<String> list = new ArrayList<String>();

        list.add(xsoup.select(s, "//span[1]").getElements().text());
//        list.add(xsoup.select(s, "//span[2]").getElements().text());
//        list.add(xsoup.select(s, "//a").getElements().text());
        Elements elements = xsoup.select(s,"//a").getElements();
        Element element = elements.get(0);
        list.add(element.attr("href"));
        list.add(element.text());

        return list;
    }
}
