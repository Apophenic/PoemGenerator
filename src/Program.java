import com.sourceclear.gramtest.*;
import org.antlr.v4.runtime.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Program
{
    public static void main(String[] args)
    {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        Option helpOption = new Option("h","help",false,"prints this message");
        Option inOption = OptionBuilder.withType(String.class)
                                       .withArgName("Input path")
                                       .hasArg()
                                       .withDescription("File path to grammatical rules file")
                                       .create("in");
        Option outOption = OptionBuilder.withType(String.class)
                                        .withArgName("Output path")
                                        .hasArg()
                                        .withDescription("File path to save bnf formatted grammar rules")
                                        .create("out");
        options.addOption(helpOption);
        options.addOption(inOption);
        options.addOption(outOption);

        try
        {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("help") || line.getOptions().length == 0)
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("gramtest [options]", options);
            }
            if(!line.hasOption("in"))
            {
                System.out.println("[ERROR] Input file not specified!");
                return;
            }
            if(!line.hasOption("out"))
            {
                System.out.println("[ERROR] Output file not specified!");
                return;
            }

            String in = line.getOptionValue("in");
            String out = line.getOptionValue("out");

            generateBNF(in, out);

            generatePoemFromBNF(out);
        }
        catch (ParseException e)
        {
            System.out.println("[ERROR] Failed to parse rules file!");
        }
    }

    public static void generateBNF(String pathIn, String pathOut)
    {
        try
        {
            Scanner s = new Scanner(new File(pathIn));
            StringBuilder sb = new StringBuilder();

            while(s.hasNextLine())
            {
                String line = s.nextLine();

                String[] arr = line.split(" ");
                for(String token : arr)
                {
                    if(token.contains(":"))
                        token = '<' + token.replace(":", "> ::=");
                    if(token.contains("|"))
                        token = '(' + token + ')' + " <WS>";

                    token = token.replace("$END", "<END>")
                                 .replace("$LINEBREAK", "<LINEBREAK>");

                    sb.append(token + " ");
                }

                sb.append('\n');
            }

            sb.append("<LINEBREAK> ::= _n\n");
            sb.append("<WS> ::= \" \"\n");
            sb.append("<END> ::= ");

            writeBNF(sb.toString(), pathOut);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("[ERROR] Failed to find file: " + pathIn);
        }
    }

    private static void generatePoemFromBNF(String path)
    {
        bnfLexer lexer = null;
        try
        {
            lexer = new bnfLexer(new ANTLRFileStream(path));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            bnfParser grammarParser = new bnfParser(tokens);
            ParserRuleContext tree = grammarParser.rulelist();
            GeneratorVisitor extractor = new GeneratorVisitor(1, 2, false);
            extractor.visit(tree);

            extractor.printTests();
        }
        catch (IOException e)
        {
            System.out.println("[ERROR] Failed to find file: " + path);
        }
    }

    private static void writeBNF(String text, String path)
    {
        try
        {
            Files.write(Paths.get(path), text.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("[ERROR] Failed to find file: " + path);
        }
    }
}
