package net.consensys.eventeum.integration.eventstore.jpa;

import junit.framework.TestCase;
import net.consensys.eventeum.dto.event.parameter.AbstractEventParameter;
import net.consensys.eventeum.dto.event.parameter.EventParameter;
import net.consensys.eventeum.dto.event.parameter.NumberParameter;
import net.consensys.eventeum.dto.event.parameter.StringParameter;
import net.consensys.eventeum.integration.eventstore.jpa.model.EventParameterEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class JpaEventStoreTest {
    @Test
    public void testToEntityParameters1() {
        EventParameterEntity eventParameterEntity = new JpaEventStore().toEntityParameter(new StringParameter("t1", "v1"), true);
        assertEquals("t1",eventParameterEntity.getType());
        assertEquals("v1",eventParameterEntity.getStringValue());
        assertEquals(0,eventParameterEntity.getIntValue());
        assertEquals(true,eventParameterEntity.isIndexed());
    }

    @Test
    public void testToEntityParameters2() {
        EventParameterEntity eventParameterEntity = new JpaEventStore().toEntityParameter(new StringParameter("t1", "v1"), false);
        assertEquals("t1",eventParameterEntity.getType());
        assertEquals("v1",eventParameterEntity.getStringValue());
        assertEquals(0,eventParameterEntity.getIntValue());
        assertEquals(false,eventParameterEntity.isIndexed());
    }

   @Test
    public void testToEntityParameters3() {
        EventParameterEntity eventParameterEntity = new JpaEventStore().toEntityParameter(new NumberParameter("t1", BigInteger.valueOf(3)), true);
        assertEquals("t1",eventParameterEntity.getType());
        assertEquals(null, eventParameterEntity.getStringValue());
        assertEquals(3,eventParameterEntity.getIntValue());
        assertEquals(true,eventParameterEntity.isIndexed());
    }

   @Test
    public void testToEntityParameters4() {
        EventParameterEntity eventParameterEntity = new JpaEventStore().toEntityParameter(new NumberParameter("t1", BigInteger.valueOf(3)), false);
        assertEquals("t1",eventParameterEntity.getType());
        assertEquals(null, eventParameterEntity.getStringValue());
        assertEquals(3,eventParameterEntity.getIntValue());
        assertEquals(false,eventParameterEntity.isIndexed());
    }

    @Test
    public void testToEntityParameters() {
        List<EventParameter> indexedParameters = Arrays.asList(new EventParameter[]{
                new StringParameter("ti1", "v1"),
                new NumberParameter("ti2", BigInteger.valueOf(3)),
                new StringParameter("ti3", "v1"),
                new NumberParameter("ti4", BigInteger.valueOf(6))
        });
        List<EventParameter> nonIndexedParameters = Arrays.asList(new EventParameter[]{
                new StringParameter("tni1", "v1"),
                new NumberParameter("tni2", BigInteger.valueOf(3)),
                new StringParameter("tni3", "v1"),
                new NumberParameter("tni4", BigInteger.valueOf(6))
        });
        List<EventParameterEntity> eventParameterEntities = new JpaEventStore().toEntityParameters(indexedParameters, nonIndexedParameters);
        assertEquals(8, eventParameterEntities.size());
    }

    @Test
    public void testToEventParameterString() {
        EventParameterEntity epe = new EventParameterEntity();
        epe.setIntValue(0);
        epe.setStringValue("whatever");
        epe.setType("someType");

        EventParameter ep = new JpaEventStore().toEventParameter(epe);

        assertTrue(ep instanceof StringParameter);
        assertEquals("whatever", ((StringParameter) ep).getValue());
        assertEquals("someType", ep.getType());
    }

    @Test
    public void testToEventParameterNumber() {
        EventParameterEntity epe = new EventParameterEntity();
        epe.setIntValue(12);
        epe.setStringValue(null);
        epe.setType("someType");

        EventParameter ep = new JpaEventStore().toEventParameter(epe);

        assertTrue(ep instanceof NumberParameter);
        assertEquals(12, ((NumberParameter) ep).getValue().longValue());
        assertEquals("someType", ep.getType());
    }

    @Test
    public void testToIndexedParameters() {
        List<EventParameterEntity> input = Arrays.asList(
                buildEventParameterEntity(12, "", "type", true),
                buildEventParameterEntity(62, "", "type", false),
                buildEventParameterEntity(15, "", "type", true),
                buildEventParameterEntity(0, "toto", "type", true),
                buildEventParameterEntity(0, "titi", "type", false)
        );
        List<EventParameter> eventParameters = new JpaEventStore().toIndexedventParameters(input);
        assertEquals(3, eventParameters.size());
    }

    @Test
    public void testToNonIndexedParameters() {
        List<EventParameterEntity> input = Arrays.asList(
                buildEventParameterEntity(12, "", "type", true),
                buildEventParameterEntity(62, "", "type", false),
                buildEventParameterEntity(15, "", "type", true),
                buildEventParameterEntity(0, "toto", "type", true),
                buildEventParameterEntity(0, "titi", "type", false)
        );
        List<EventParameter> eventParameters = new JpaEventStore().toNonIndexedventParameters(input);
        assertEquals(2, eventParameters.size());
    }

    private EventParameterEntity buildEventParameterEntity(long numberValue, String stringValue, String type, boolean indexed) {
        EventParameterEntity epe = new EventParameterEntity();
        epe.setIntValue(numberValue);
        epe.setStringValue(stringValue);
        epe.setType(type);
        epe.setIndexed(indexed);
        return epe;
    }
}
