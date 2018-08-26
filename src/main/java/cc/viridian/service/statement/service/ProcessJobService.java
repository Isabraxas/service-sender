package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.repository.UpdateJobProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ProcessJobService {

    private UpdateJobProducer updateJobProducer;

    //private CorebankAdapterConfig corebankAdapterConfig;

    @Autowired
    public ProcessJobService(UpdateJobProducer updateJobProducer /*, CorebankAdapterConfig corebankAdapterConfig */) {
        this.updateJobProducer = updateJobProducer;
        //this.corebankAdapterConfig = corebankAdapterConfig;
    }

    public UpdateJobTemplate process(final SenderTemplate data) {
        log.info("process formatter : " + data.getAccount() + " " + data.getFormatAdapter());
        log.info("process sender : " + data.getAccount() + " " + data.getSendAdapter());

        /*
        PdfGenerator pdfGenerator = new PdfGenerator();
        byte[] res;
        res = pdfGenerator.generateDocument(data.getStatement());
        String output = pdfGenerator.receiveDocument(res);
*/
        //todo:
        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("sender");
        updateJob.setAdapterCode(data.getFormatAdapter());
        updateJob.setErrorCode("");
        updateJob.setErrorDesc("sent by email");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;

/*
        CoreBank coreBank = corebankAdapterConfig.getCorebankAdapter(data.getCorebankAdapter());
        if (coreBank == null) {
            return sendInvalidCorebankAdapter(data);
        }

        log.debug("Corebank adapter class: " + coreBank.getClass().getName());
        log.debug("processing statement from " + data.getDateFrom() + " to " + data.getDateTo());

        GetStatementResponse response = coreBank.getStatement(data.getAccount(), data.getType(),
            data.getCurrency(), data.getDateFrom(), data.getDateTo() );

        Statement statement = response.getStatement();

        if (response.getWasThereError()) {
            if (response.getErrorCode().equalsIgnoreCase("invalid-adapter")) {
                return sendInvalidCorebankAdapter(data);
            }
            if (response.getErrorCode().equalsIgnoreCase("invalid-account")) {
                return sendInvalidAccount(data);
            }
            if (response.getErrorCode().equalsIgnoreCase("network-error")) {
                return sendNetworkErrorUpdateJob(data);
            }
            if (response.getErrorCode().equalsIgnoreCase("database-error")) {
                return sendDatabaseErrorUpdateJob(data);
            }

        } else {
            if (response.getStatement() != null) {
                log.debug(response.getStatement().toString());
                //send the statement to sender queue
                statementProducer.send(data.getId().toString(), statement);
                return sendNormalUpdateJob(data);
            }
            //error
        }
        log.error("invalid response " +  response.getErrorCode());
        return null;  //shouldn't happen
        */
    }

    //return with Normal update
    private UpdateJobTemplate sendNormalUpdateJob(final JobTemplate data) {
        log.info("send updateJob for account: " + data.getAccount() + " " + data.getCorebankAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode("");
        updateJob.setErrorDesc("");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }

    //return with invalid corebank adapter
    private UpdateJobTemplate sendInvalidCorebankAdapter(final JobTemplate data) {
        log.error(
            "account " + data.getAccount() + " has an invalid or not loaded corebank adapter: "
                + data.getCorebankAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode("invalid-adapter");
        updateJob.setErrorDesc("adapter " + data.getCorebankAdapter() + " is invalid or not loaded");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }

    //return with invalid account in the remote corebank (closed, unexistent, invalid account, type or currency)
    private UpdateJobTemplate sendInvalidAccount(final JobTemplate data) {
        String message = "account " + data.getAccount() + " "
            + data.getCurrency() + " "
            + data.getType() + " is invalid on remote corebank: "
            + data.getCorebankAdapter();
        log.error(message);

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode("invalid-account");
        updateJob.setErrorDesc(message);
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(false);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }

    //return with network error
    private UpdateJobTemplate sendNetworkErrorUpdateJob(final JobTemplate data) {
        log.error("network error processing account: " + data.getAccount() + " " + data.getCorebankAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode("network-error");
        updateJob.setErrorDesc("network error processing with adapter " + data.getCorebankAdapter() + "");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(true);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }

    //return with database error
    private UpdateJobTemplate sendDatabaseErrorUpdateJob(final JobTemplate data) {
        log.error("database error processing account: " + data.getAccount() + " " + data.getCorebankAdapter());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode("database-error");
        updateJob.setErrorDesc("database error processing with adapter " + data.getCorebankAdapter() + "");
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(true);

        updateJobProducer.send(data.getId().toString(), updateJob);
        return updateJob;
    }
}
