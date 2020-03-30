package com.anyservice.service.user;

import com.anyservice.dto.user.UserDetailed;
import com.anyservice.service.api.ICustomMailSender;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
public class CustomMailSender implements ICustomMailSender {

    private final JavaMailSender sender;
    private final Configuration freemarkerConfig;
    private final ConversionService conversionService;
    private final MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String mailLogin;

    @Value("${spring.application.url}")
    private String applicationUrl;

    @Value("${spring.application.test}")
    private boolean applicationTest;

    public CustomMailSender(JavaMailSender sender, Configuration freemarkerConfig,
                            ConversionService conversionService, MessageSource messageSource) {
        this.sender = sender;
        this.freemarkerConfig = freemarkerConfig;
        this.conversionService = conversionService;
        this.messageSource = messageSource;
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/");
    }

    @Override
    public void sendVerificationCode(UserDetailed user, UUID verificationCode) {
        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Prepare variables for a message
        Map<String, Object> model = prepareModel(user, verificationCode);

        // Get user email address
        String userEmailAddress = user.getContacts().getEmail();

        try {
            // Get template
            Template t = freemarkerConfig.getTemplate("/mail/verification.ftl");

            // Set prepared variables to template and convert it to string
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            // Fill the message
            helper.setFrom(mailLogin);
            helper.setTo(userEmailAddress);
            helper.setText(text, true); // set to html
            helper.setSubject(messageSource.getMessage("user.verification.email.subject",
                    null, LocaleContextHolder.getLocale()));

            // To not to spam emails and not to be blocked by mail server during tests
            if (!applicationTest) {
                // Send message
                sender.send(message);
            }
        } catch (Throwable e) {
            String errorMessage = messageSource.getMessage("user.verification.email.error",
                    new Object[]{e.getMessage()}, LocaleContextHolder.getLocale());

            log.error(errorMessage, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Prepare a set of variables (model) to be set to template further
     *
     * @param user             user
     * @param verificationCode verification code
     * @return map of variables_names - variables_values for message template
     */
    private Map<String, Object> prepareModel(UserDetailed user, UUID verificationCode) {
        Map<String, Object> model = new HashMap<>();

        String initials = conversionService.convert(user.getInitials(), String.class);
        String verificationLink = buildVerificationLink(user.getUuid(), verificationCode);

        // Text messages on specific language
        String greetings = messageSource.getMessage("user.verification.email.greetings",
                null, LocaleContextHolder.getLocale());
        String explanation = messageSource.getMessage("user.verification.email.explanation",
                null, LocaleContextHolder.getLocale());
        String whatToDo = messageSource.getMessage("user.verification.email.whattodo",
                null, LocaleContextHolder.getLocale());
        String linkText = messageSource.getMessage("user.verification.email.linktext",
                null, LocaleContextHolder.getLocale());
        String ifWrongAddress = messageSource.getMessage("user.verification.email.ifwrongaddress",
                null, LocaleContextHolder.getLocale());
        String companyCredentials = messageSource.getMessage("user.verification.email.companycredentials",
                null, LocaleContextHolder.getLocale());

        model.put("greetings", greetings);
        model.put("initials", initials);
        model.put("explanation", explanation);
        model.put("whattodo", whatToDo);
        model.put("link", verificationLink);
        model.put("linktext", linkText);
        model.put("ifwrongaddress", ifWrongAddress);
        model.put("companycredentials", companyCredentials);

        return model;
    }

    /**
     * Build verification link, clicked on that user can verify it's account
     *
     * @param uuid             user identifier
     * @param verificationCode code for verification of user's account
     * @return complete url for clicking
     */
    private String buildVerificationLink(UUID uuid, UUID verificationCode) {
        return applicationUrl +
                "/user/verification" +
                "/" + uuid +
                "/" + verificationCode;
    }

}
