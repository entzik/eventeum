package net.consensys.eventeumserver.integrationtest;

import junit.framework.TestCase;
import net.consensys.eventeum.chain.util.Web3jUtil;
import net.consensys.eventeum.dto.event.ContractEventDetails;
import net.consensys.eventeum.dto.event.ContractEventStatus;
import net.consensys.eventeum.dto.event.filter.ContractEventFilter;
import net.consensys.eventeum.integration.eventstore.db.repository.ContractEventDetailsRepository;
import net.consensys.eventeum.integration.eventstore.jpa.model.ContractEventDetailsEntity;
import net.consensys.eventeum.integration.eventstore.jpa.model.EventParameterEntity;
import net.consensys.eventeum.integration.eventstore.jpa.repository.ContractEventDetailsJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test-jpa.properties")
public class ContractEventDetailsJpaRepositoryIT extends BaseFromBlockIntegrationTest {

    @Autowired
    private ContractEventDetailsJpaRepository repo;

    @Test
    public void testSaveContractEventDetailsEntity() {
        ContractEventDetailsEntity eventDetailsEntity = buildContractEventDetailsEntity(0);

        ContractEventDetailsEntity saved = repo.save(eventDetailsEntity);

        assertNotNull(saved.getId());
        assertEquals(0, saved.getVersion());
        assertEquals(13, saved.getLogIndex());
        assertEquals(231, saved.getBlockNumber());
        assertEquals("myName0", saved.getName());
        assertEquals("specEventSig0", saved.getEventSpecificationSignature());
        assertEquals("txhash0", saved.getTransactionHash());
        assertEquals("blockHash0", saved.getBlockHash());
        assertEquals("filterID0", saved.getFilterId());
        assertEquals("address0", saved.getAddress());
        assertEquals(2, saved.getParameters().size());

        EventParameterEntity epe0 = saved.getParameters().get(0);
        EventParameterEntity epe1 = saved.getParameters().get(1);

        assertEquals("type1", epe0.getType());
        assertTrue(epe0.isIndexed());
        assertNull(epe0.getStringValue());
        assertEquals(12, epe0.getIntValue());

        assertEquals("type2", epe1.getType());
        assertFalse(epe1.isIndexed());
        assertEquals("someValue", epe1.getStringValue());
        assertEquals(0, epe1.getIntValue());
    }

    @Test
    public void testFindByEventSpecificationSignature() {
        for (int i = 1; i <= 50; i ++) {
            ContractEventDetailsEntity eventDetailsEntity = buildContractEventDetailsEntity(i);
            repo.save(eventDetailsEntity);
        }

        Page<ContractEventDetailsEntity> events = repo.findByEventSpecificationSignature("specEventSig3", PageRequest.of(0, 10));

        assertEquals(5, events.getContent().size());
        for (int i = 0; i < 5; i ++) {
            ContractEventDetailsEntity saved = events.getContent().get(i);
            assertEquals("specEventSig3", saved.getEventSpecificationSignature());
        }
    }

    @NotNull
    private ContractEventDetailsEntity buildContractEventDetailsEntity(int offset) {
        ArrayList<EventParameterEntity> parameters = new ArrayList<>();

        EventParameterEntity ep1 = new EventParameterEntity();
        ep1.setIndexed(true);
        ep1.setType("type1");
        ep1.setStringValue(null);
        ep1.setIntValue(12);
        parameters.add(ep1);

        EventParameterEntity ep2 = new EventParameterEntity();
        ep2.setIndexed(false);
        ep2.setType("type2");
        ep2.setStringValue("someValue");
        ep2.setIntValue(0);
        parameters.add(ep2);

        ContractEventDetailsEntity eventDetailsEntity = new ContractEventDetailsEntity();
        eventDetailsEntity.setName("myName" + offset);
        eventDetailsEntity.setEventSpecificationSignature("specEventSig" + (offset % 10));
        eventDetailsEntity.setTransactionHash("txhash" + offset);
        eventDetailsEntity.setLogIndex(13 + offset);
        eventDetailsEntity.setParameters(parameters);
        eventDetailsEntity.setBlockNumber(231 + offset);
        eventDetailsEntity.setBlockHash("blockHash" + offset);
        eventDetailsEntity.setFilterId("filterID" + offset);
        eventDetailsEntity.setAddress("address" + offset);
        eventDetailsEntity.setStatus(ContractEventStatus.CONFIRMED);
        return eventDetailsEntity;
    }
}
