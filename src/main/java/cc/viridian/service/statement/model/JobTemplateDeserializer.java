package cc.viridian.service.statement.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class JobTemplateDeserializer extends JsonDeserializer<JobTemplate> {

    @Override
    public JobTemplate deserialize(final JsonParser parser, final DeserializationContext ctxt,
                                   final JobTemplate intoValue) throws IOException {
        return super.deserialize(parser, ctxt, intoValue);
    }

    @Override
    public JobTemplate deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        System.out.println(node);
        System.out.println(node.get("dateFrom"));

        JobTemplate jobTemplate = new JobTemplate();
        jobTemplate.setCorebankAdapter("x");
        jobTemplate.setAccount(node.get("account").toString());
        jobTemplate.setCurrency(node.get("currency").toString());
        jobTemplate.setType(node.get("type").toString());

        return jobTemplate;
        //return null;
    }
}
