package run.halo.app.core.freemarker.tag;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;
import run.halo.app.cache.CacheStore;
import run.halo.app.model.enums.PostStatus;
import run.halo.app.service.OptionService;
import run.halo.app.service.PostService;


/**
 * @author SakuraDon
 */
@Component
public class BlogTagDirective implements TemplateDirectiveModel {

    private final PostService postService;
    private final OptionService optionService;
    private final CacheStore<String, String> cacheStore;
    private static final String ARTICLES_CACHE = "ARTICLES_CACHE";
    private static final String VIEWS_CACHE = "VIEWS_CACHE";
    private static final long CACHE_TIMEOUT = 1;

    public BlogTagDirective(Configuration configuration, PostService postService,
        OptionService optionService,
        CacheStore<String, String> cacheStore) {
        this.postService = postService;
        this.optionService = optionService;
        this.cacheStore = cacheStore;
        configuration.setSharedVariable("blogTag", this);
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
        TemplateDirectiveBody body) throws TemplateException, IOException {
        final DefaultObjectWrapperBuilder builder =
            new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
        String articles = cacheStore.get(ARTICLES_CACHE)
            .orElseGet(() -> {
                String value = String.valueOf(postService.countByStatus(PostStatus.PUBLISHED));
                cacheStore.put(ARTICLES_CACHE, value, CACHE_TIMEOUT, TimeUnit.MINUTES);
                return value;
            });
        String views = cacheStore.get(VIEWS_CACHE)
            .orElseGet(() -> {
                String value = String.valueOf(postService.countVisit());
                cacheStore.put(VIEWS_CACHE, value, CACHE_TIMEOUT, TimeUnit.MINUTES);
                return value;
            });
        long birthday = optionService.getBirthday();
        long days = (System.currentTimeMillis() - birthday) / (1000 * 24 * 3600);
        double years = (double) (days * 10 / 365) / 10;
        env.setVariable("articles", builder.build().wrap(articles));
        env.setVariable("views", builder.build().wrap(views));
        env.setVariable("days", builder.build().wrap(days));
        env.setVariable("years", builder.build().wrap(years));
        body.render(env.getOut());
    }

}
