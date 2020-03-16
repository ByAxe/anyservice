package com.anyservice.service.validators.api;

import org.springframework.context.MessageSource;

import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

public abstract class AValidator<E> {

    protected abstract MessageSource getMessageSource();

    /**
     * Ensure that given field contains only letters
     *
     * @param field     field content REQUIRED
     * @param fieldName field name REQUIRED
     * @param errors    errors during validation
     */
    public void validateLettersOnlyField(String field, String fieldName, Map<String, Object> errors) {
        // Check whether filedName is present
        if (fieldName == null || fieldName.isEmpty()) {
            errors.put("fieldName", getMessageSource().getMessage("user.letter.only.field.fieldname",
                    new Object[]{"fieldName"}, getLocale()));
            return;
        }

        // Check whether field is present
        if (field == null || field.isEmpty()) {
            errors.put(fieldName, getMessageSource().getMessage("user.letter.only.field.empty",
                    new Object[]{fieldName}, getLocale()));
            return;
        }

        // Ensure that the field contains only letters
        for (char ch : field.toCharArray()) {
            if (!Character.isLetter(ch)) {
                errors.put(fieldName, getMessageSource().getMessage("user.letter.only.field",
                        new Object[]{fieldName}, getLocale()));
                return;
            }
        }
    }
}
