package cc.viridian.service.statement.service;

import cc.viridian.provider.model.StatementDocument;
import cc.viridian.provider.payload.GetFormatterResponse;
import cc.viridian.provider.payload.ResponseAdapterCode;
import cc.viridian.provider.payload.SendStatementResponse;
import cc.viridian.provider.spi.StatementFormatter;
import cc.viridian.provider.spi.StatementSender;
import cc.viridian.service.statement.config.FormatterAdapterConfig;
import cc.viridian.service.statement.config.SenderAdapterConfig;
import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.repository.UpdateJobProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ProcessSenderService {

    private UpdateJobProducer updateJobProducer;
    //todo Retry Producer

    private FormatterAdapterConfig formatterAdapterConfig;
    private SenderAdapterConfig senderAdapterConfig;

    @Autowired
    public ProcessSenderService(UpdateJobProducer updateJobProducer , FormatterAdapterConfig formatterAdapterConfig,
                                SenderAdapterConfig senderAdapterConfig) {
        this.updateJobProducer = updateJobProducer;
        this.formatterAdapterConfig = formatterAdapterConfig;
        this.senderAdapterConfig = senderAdapterConfig;
    }

    public UpdateJobTemplate process(final SenderTemplate data) {
        log.info("process generateDocument: " + data.getAccount() + " " + data.getFormatAdapter()
                     + " " + data.getDateFrom() + " " + data.getDateTo());

        StatementFormatter formatter = formatterAdapterConfig.getFormatterAdapter(data.getFormatAdapter());
        if (formatter == null) {
            return sendInvalidFormatterAdapter(data);
        }

        GetFormatterResponse formatterResponse = new GetFormatterResponse();

        byte[] byteDocument;
        log.debug("adapter class: " + formatter.getClass().getName());

        formatterResponse = formatter.generateDocument(data.getStatement());

        log.info("error code: " + formatterResponse.getErrorCode());
        log.info("error desc: " + formatterResponse.getErrorDesc());

        //todo: try catch IO/DB errors
        sendFormatterUpdateJob(data, formatterResponse);

        log.info("process sendDocument: " + data.getAccount() + " " + data.getSendAdapter());
        StatementSender sender = senderAdapterConfig.getSenderAdapter(data.getSendAdapter());
        if (sender == null) {
            return sendInvalidSenderAdapter(data);
        }

        SendStatementResponse senderResponse = new SendStatementResponse();

        log.debug("adapter class: " + sender.getClass().getName());

        StatementDocument statementDocument = new StatementDocument();
        statementDocument.setTitle(data.getStatement().getHeader().getStatementTitle());
        statementDocument.setAccount(data.getAccount());
        statementDocument.setRecipientName(data.getRecipient());
        statementDocument.setDocument(formatterResponse.getBytesDocument());
        statementDocument.setMimetype(formatterResponse.getMimetype());
        statementDocument.setFilename(formatterResponse.getFilename());
        statementDocument.setRecipientAccount(data.getRecipient());

        senderResponse = sender.sendStatement(statementDocument);

        log.info("error code: " + senderResponse.getErrorCode());
        log.info("error desc: " + senderResponse.getErrorDesc());

        return sendSenderUpdateJob(data, senderResponse);
    }

    private UpdateJobTemplate sendFormatterUpdateJob(final SenderTemplate senderTemplate,
                                                     final GetFormatterResponse formatterResponse) {
        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId( senderTemplate.getId());
        updateJob.setAccount(senderTemplate.getAccount());
        updateJob.setAdapterType("formatter");
        updateJob.setAdapterCode(ResponseAdapterCode.ADAPTER_FORMATTER.name());
        updateJob.setErrorCode(formatterResponse.getErrorCode().name());
        updateJob.setErrorDesc(formatterResponse.getErrorDesc());
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(senderTemplate.getId().toString(), updateJob);
        return updateJob;
    }

    private UpdateJobTemplate sendSenderUpdateJob(final SenderTemplate senderTemplate,
                                                     final SendStatementResponse senderResponse) {
        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId( senderTemplate.getId());
        updateJob.setAccount(senderTemplate.getAccount());
        updateJob.setAdapterType("sender");
        updateJob.setAdapterCode(ResponseAdapterCode.ADAPTER_SENDER.name());
        updateJob.setErrorCode(senderResponse.getErrorCode().name());
        updateJob.setErrorDesc(senderResponse.getErrorDesc());
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(senderResponse.getShouldRetryAgain());

        updateJobProducer.send(senderTemplate.getId().toString(), updateJob);
        return updateJob;
    }

    private UpdateJobTemplate sendInvalidFormatterAdapter(final SenderTemplate data) {
        log.error(
            "account " + data.getAccount() + " has an invalid or not loaded formatter adapter: "
                + data.getFormatAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("formatter");
        updateJob.setAdapterCode(data.getFormatAdapter());
        updateJob.setErrorCode("invalid-adapter");
        updateJob.setErrorDesc("adapter " + data.getFormatAdapter() + " is invalid or not loaded");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }

    private UpdateJobTemplate sendInvalidSenderAdapter(final SenderTemplate data) {
        log.error(
            "account " + data.getAccount() + " has an invalid or not loaded formatter adapter: "
                + data.getFormatAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("sender");
        updateJob.setAdapterCode(data.getSendAdapter());
        updateJob.setErrorCode("invalid-adapter");
        updateJob.setErrorDesc("adapter " + data.getSendAdapter() + " is invalid or not loaded");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }
}
