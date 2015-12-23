import com.sourceclear.gramtest.*;
import junit.framework.TestCase;
import org.antlr.v4.runtime.*;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;


public class Test extends TestCase
{
    public Test(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /** Verifies only one poem is being generated
     *
     * @throws IOException
     */
    public void testPoemCount() throws IOException
    {
        Lexer lexer = new bnfLexer(new ANTLRInputStream(getClass().getResourceAsStream("res/grammar_test.bnf")));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        bnfParser grammarParser = new bnfParser(tokens);
        ParserRuleContext tree = grammarParser.rulelist();
        GeneratorVisitor extractor = new GeneratorVisitor();
        extractor.visit(tree);
        List<String> generatedTests = extractor.getTests();

        Assert.assertEquals(1, generatedTests.size());
    }

    /** Verifies tokens are being tokenized correctly
     *
     * @throws IOException
     */
    public void testTokenCount() throws IOException
    {
        Lexer lexer = new bnfLexer(new ANTLRInputStream(getClass().getResourceAsStream("res/grammar_test.bnf")));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        bnfParser grammarParser = new bnfParser(tokens);
        ParserRuleContext tree = grammarParser.rulelist();

        Assert.assertEquals(223, tokens.size());
    }

    /** Verifies rules are being parsed correctly
     *
     * @throws IOException
     */
    public void testRuleCount() throws IOException
    {
        Lexer lexer = new bnfLexer(new ANTLRInputStream(getClass().getResourceAsStream("res/grammar_test.bnf")));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        bnfParser grammarParser = new bnfParser(tokens);
        ParserRuleContext tree = grammarParser.rulelist();

        Assert.assertEquals(11, tree.getChildCount());
    }
}
