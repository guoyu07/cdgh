package cn.ihuhai.spider;

import cn.ihuhai.model.BuildingProjectTr;
import cn.ihuhai.selector.BuildingProjectTrSelector;
import com.alibaba.fastjson.JSONWriter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.xsoup.Xsoup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GithubRepoPageProcessor implements PageProcessor {
    private static Logger LOGGER = LoggerFactory.getLogger(GithubRepoPageProcessor.class);

    private final static String INFO_LIST_URL_PREFIX = "http://www.cdgh.gov.cn/info/iList.jsp?site_id=CMSCMScgh&cat_id=10063&cur_page=";
    private final static String PROJECT_URL_PREFIX = "http://www.cdgh.gov.cn/ghgs/jzxmgg/";
    private final static String IMAGE_PAGE_URL_PREFIX = "http://www.cdgh.gov.cn/info/getPic.jsp?";
    private final static String DOMAIN_PREFIX = "http://www.cdgh.gov.cn";
    private Site site = Site.me().setRetryTimes(3).setSleepTime(300);
    private Map<String, BuildingProjectTr> detailMap = new ConcurrentHashMap<String, BuildingProjectTr>();
    private Map<String, BuildingProjectTr> imagePageMap = new ConcurrentHashMap<String, BuildingProjectTr>();
    private Xsoup pageXsoup = new Xsoup();
    private AtomicInteger aint = new AtomicInteger();

    @Override
    public void process(Page page) {
        /*page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));*/
        Html html  = page.getHtml();
        String pageUrl = page.getUrl().toString();
        if(pageUrl.startsWith(INFO_LIST_URL_PREFIX)){
            Selectable s1 = html.$("ul.commonList_dot li");
            Selector trSelector = new BuildingProjectTrSelector();
            List<String> rows = s1.selectList(trSelector).all();
//            List<BuildingProjectTr> trs = new ArrayList<BuildingProjectTr>();
            int columns = 3;
            for (int i = 0; i < rows.size()/ columns; i++){
                BuildingProjectTr tr = new BuildingProjectTr();
                tr.setPageUrl(pageUrl);
                tr.setDate(rows.get(i * columns));
                tr.setDetailUrl(rows.get(i * columns + 1));
                tr.setProjectName(rows.get(i * columns + 2));
                tr.setImageUrls(Collections.synchronizedList(new ArrayList<String>()));
                tr.setId(tr.getDetailUrl());
                detailMap.put(tr.getDetailUrl(), tr);
                page.addTargetRequest(tr.getDetailUrl());
            }
//            page.putField("trs", trs);
        }else if (pageUrl.startsWith(PROJECT_URL_PREFIX)){
            BuildingProjectTr tr = detailMap.get(pageUrl);
            List<String> links = html.$("a[onclick~=setUrl.*]").all();
            if(CollectionUtils.isNotEmpty(links)){
                for (String link : links){
                    String onclick = pageXsoup.select(link, "a").getElements().attr("onclick");
                    String imagePageUrl = DOMAIN_PREFIX + (onclick.replace("setUrl('", "").replace("');", ""));
                    imagePageMap.put(imagePageUrl, tr);
//                    page.putField("imageUrl", imagePageUrl);
                    page.addTargetRequest(imagePageUrl);
                }
            }
        }else if(pageUrl.startsWith(IMAGE_PAGE_URL_PREFIX)){
            BuildingProjectTr tr = imagePageMap.get(pageUrl);

            String imageUrl = pageXsoup.select(html.get(), "img").getElements().attr("src");
            tr.getImageUrls().add(imageUrl);

//            Map<String,Object> index = new HashMap<String, Object>();
//            index.put("overwrite", true);
//            index.put("doc", tr);
//            page.putField("add", index);
        }
    }

    public Collection<BuildingProjectTr> getDocs(){
        return detailMap.values();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws IOException {
        long t1 = System.currentTimeMillis();
        GithubRepoPageProcessor processor = new GithubRepoPageProcessor();
        Spider spider = Spider.create(processor);
        int startPage = 1;
        int endPage = 97;
        for (int page = startPage; page <= endPage; page++){
            spider.addUrl(INFO_LIST_URL_PREFIX + page);
        }
        spider.thread(5).run();

        String savePath = "D:\\data\\webmagic\\" + spider.getSite().getDomain() + "\\" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + ".json";
        Collection<BuildingProjectTr> trs = processor.getDocs();
        JSONWriter writer = new JSONWriter(new BufferedWriter(new FileWriter(savePath)));
        writer.writeObject(trs);
        writer.close();

        long t2 = System.currentTimeMillis();
        LOGGER.info("================== get " + trs.size() +" doc(s) in " + (t2 - t1) + " ms ==================");
    }
}