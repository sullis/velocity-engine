package org.apache.velocity.test.util.introspection;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.util.introspection.*;
import org.apache.velocity.test.BaseTestCase;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;

/**
 * Tests uberspectors chaining
 */
public class ChainedUberspectorsTestCase extends BaseTestCase {

    public ChainedUberspectorsTestCase(String name)
    	throws Exception
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ChainedUberspectorsTestCase.class);
    }

    public void setUp()
            throws Exception
    {
        Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
        Velocity.addProperty(Velocity.UBERSPECT_CLASSNAME,"org.apache.velocity.util.introspection.UberspectImpl");
        Velocity.addProperty(Velocity.UBERSPECT_CLASSNAME,"org.apache.velocity.test.util.introspection.ChainedUberspectorsTestCase$ChainedUberspector");
        Velocity.addProperty(Velocity.UBERSPECT_CLASSNAME,"org.apache.velocity.test.util.introspection.ChainedUberspectorsTestCase$LinkedUberspector");
	    Velocity.init();
    }

    public void tearDown()
    {
    }

    public void testChaining()
    	throws Exception
    {
        VelocityContext context = new VelocityContext();
        context.put("foo",new Foo());
        StringWriter writer = new StringWriter();

        Velocity.evaluate(context,writer,"test","$foo.zeMethod()");
        assertEquals(writer.toString(),"ok");

        Velocity.evaluate(context,writer,"test","#set($foo.foo = 'someValue')");

        writer = new StringWriter();
        Velocity.evaluate(context,writer,"test","$foo.bar");
        assertEquals(writer.toString(),"someValue");

        writer = new StringWriter();
        Velocity.evaluate(context,writer,"test","$foo.foo");
        assertEquals(writer.toString(),"someValue");
    }

    // replaces getFoo by getBar
    public static class ChainedUberspector extends AbstractChainableUberspector
    {
        public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info info) throws Exception
        {
            identifier = identifier.replaceAll("foo","bar");
            return inner.getPropertySet(obj,identifier,arg,info);
        }
    }

    // replaces setFoo by setBar
    public static class LinkedUberspector extends UberspectImpl
    {
        public VelPropertyGet getPropertyGet(Object obj, String identifier, Info info) throws Exception
        {
            identifier = identifier.replaceAll("foo","bar");
            return super.getPropertyGet(obj,identifier,info);
        }
    }

    public static class Foo
    {
        private String bar;

        public String zeMethod() { return "ok"; }
        public String getBar() { return bar; }
        public void setBar(String s) { bar = s; }
    }

}