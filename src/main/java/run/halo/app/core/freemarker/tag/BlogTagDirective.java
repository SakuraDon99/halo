package run.halo.app.core.freemarker.tag;

import java.io.IOException;
import java.util.Map;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;
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

    public BlogTagDirective(Configuration configuration, PostService postService,
        OptionService optionService) {
        this.postService = postService;
        this.optionService = optionService;
        configuration.setSharedVariable("blogTag", this);
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
        TemplateDirectiveBody body) throws TemplateException, IOException {
        final DefaultObjectWrapperBuilder builder =
            new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
        long articles = postService.countByStatus(PostStatus.PUBLISHED);
        long views = postService.countVisit();
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
