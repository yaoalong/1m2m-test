package lab.mars.m2m.test;

import lab.mars.m2m.nami.util.registry.HierarchyKey;
import lab.mars.m2m.nami.util.registry.Key;
import lab.mars.m2m.nami.util.registry.Keys;
import lab.mars.m2m.protocol.HasChildren;
import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.enumeration.m2m_resourceType;
import lab.mars.m2m.protocol.resource.m2m_resource;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * Created by haixiao on 2015/1/5.
 * test
 */
public class TestCSEBase extends TestBase {
    @Test
    public void testStart() throws Exception {
    }

    @Test
    public void testSimpleRetrieveCSE() throws Exception {
        testRetrieve(csebaseuri, null, OK);
    }

    @Test
    public void testPartialOperation() throws Exception {
        testRetrieve(csebaseuri, "getCSEBase.xml", OK);
    }

    static PrintStream printStream;

    @Test
    public void testWalkThroughCSE() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        printStream = new PrintStream(baos);
        walkThroughResource(new LinkedList<>(), 0, true, csebaseuri);
        System.out.println(baos.toString());
    }

    public static final int tapNum =1;

    public void walkThroughResource(LinkedList<Integer> prefix, int level, boolean last, String resourceURI) throws Exception {
        printPrefix(prefix, level, last);

        Key key = Keys.decode(resourceURI);
        String name;
        if (key instanceof HierarchyKey) {
            name = ((HierarchyKey) key).my.toString();
        } else {
            name = key.toString();
        }
        m2m_resource resource = testRetrieve(resourceURI, null, OK);
        printStream.println(name + "<" + m2m_resourceType.stringForType(resource.ty) + ">");

        if (resource instanceof HasChildren) {
            if (!last) prefix.addLast(level);
            List<m2m_childResourceRef> ch = ((HasChildren) resource).children();
            if (ch == null) ch = new LinkedList<>();
            int i = 0, n = ch.size();
            for (m2m_childResourceRef ref : ch) {
                boolean cLast = i == n - 1;
                int cLevel = level + 1;
                walkThroughResource(prefix, cLevel, cLast, ref.v);
                i++;
            }
            if (!last) prefix.removeLast();
        }
    }

    private static void printPrefix(List<Integer> prefix, int myLevel, boolean last) {
        if (prefix.isEmpty() && myLevel == 0) return;
        int pre = 0;
        for (Integer level : prefix) {
            nspace((tapNum + 2) * (level - pre - 1));
            nspace(tapNum);
            printStream.print("│");
            pre = level;
        }
        nspace((tapNum + 2) * (myLevel - pre - 1));
        nspace(tapNum);
        printStream.print(last ? "└" : "├");
    }

    private static void nspace(int n) {
        for (int i = 0; i < n; i++) {
            printStream.print(" ");
        }
    }

    public static void main(String[] args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        printStream = new PrintStream(baos);
        File path = new File(".");
        listPath(new LinkedList<>(), 0, true, path);
        System.out.println(baos.toString());
    }

    public static void listPath(LinkedList<Integer> prefix, int level, boolean last, File path) {
        printPrefix(prefix, level, last);
        printStream.println(path.getName());
        if (!path.isDirectory()) return;
        File files[];
        files = path.listFiles();
        Arrays.sort(files);
        if (!last) prefix.addLast(level);
        for (int i = 0; i < files.length; i++) {
            boolean cLast = i == files.length - 1;
            int cLevel = level + 1;
            listPath(prefix, cLevel, cLast, files[i]);
        }
        if (!last) prefix.removeLast();
    }
}
