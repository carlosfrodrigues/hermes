package pl.allegro.tech.hermes.api.constraints;

import pl.allegro.tech.hermes.api.ContentType;
import pl.allegro.tech.hermes.api.DeliveryType;
import pl.allegro.tech.hermes.api.Subscription;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentTypeValidator implements ConstraintValidator<ValidContentType, Subscription> {

    @Override
    public void initialize(ValidContentType constraintAnnotation) {

    }

    @Override
    public boolean isValid(Subscription subscription, ConstraintValidatorContext context) {
        return !(subscription.getDeliveryType() == DeliveryType.BATCH && subscription.getContentType() == ContentType.AVRO);
    }
}
