//
//   Accepts custom grammar rules and outputs a poem using said rules.
//
//   https://github.com/Apophenic
//
//   Copyright (c) 2015 Justin Dayer (jdayer9@gmail.com)
//
//   Permission is hereby granted, free of charge, to any person obtaining a copy
//   of this software and associated documentation files (the "Software"), to deal
//   in the Software without restriction, including without limitation the rights
//   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//   copies of the Software, and to permit persons to whom the Software is
//   furnished to do so, subject to the following conditions:
//
//   The above copyright notice and this permission notice shall be included in
//   all copies or substantial portions of the Software.
//
//   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//   THE SOFTWARE.

import com.sourceclear.gramtest.*;
import org.antlr.v4.runtime.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/** PoemGenerator entry point
 * <p>
 * Command line use: java -jar "../PoemGenerator.jar" -in "../rules.txt"
 * <p>
 * See README.md for more information
 */
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
        options.addOption(helpOption);
        options.addOption(inOption);

        try
        {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("help") || line.getOptions().length == 0)
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("poemgenerator [options]", options);
            }
            if(!line.hasOption("in"))
            {
                System.err.println("[ERROR] Input file not specified!");
                return;
            }

            String in = line.getOptionValue("in");
            String out = new File(in).getParent() + "\\grammar.bnf";

            generateBNF(in, out);

            generatePoemFromBNF(out);
        }
        catch (ParseException e)
        {
            System.err.println("[ERROR] Failed to parse rules file!");
        }
    }

    /**
     * Generates a new BNF grammar formatted file to be used
     * with gramtest from a given rules.txt file
     * @param pathIn  Literal file path to rules.txt
     * @param pathOut  Literal file path to save new BNF file
     */
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
                        token = '<' + token.replace(":", "> ::="); // Encapsulate rules with <RULE>; convert : -> ::=
                    if(token.contains("|"))
                        token = '(' + token + ')' + " <WS>"; // Group alternatives together

                    token = token.replace("$END", "<END>")  // Convert to proper rule format
                                 .replace("$LINEBREAK", "<LINEBREAK>");

                    sb.append(token + " ");
                }

                sb.append('\n');
            }

            // Assumed rules
            sb.append("<LINEBREAK> ::= _n\n");
            sb.append("<WS> ::= \" \"\n");
            sb.append("<END> ::= ");

            writeBNF(sb.toString(), pathOut);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("[ERROR] Failed to find file: " + pathIn);
        }
    }

    public static void generatePoemFromBNF(String path)
    {
        try
        {
            bnfLexer lexer = new bnfLexer(new ANTLRFileStream(path));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            bnfParser grammarParser = new bnfParser(tokens);
            ParserRuleContext tree = grammarParser.rulelist();
            GeneratorVisitor extractor = new GeneratorVisitor(1, 2, false);
            extractor.visit(tree);

            extractor.printTests();
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to find file: " + path);
        }
    }

    /**
     * Write BNF formatted rules to file
     * @param text  String containing BNF rules
     * @param path  Literal file path to save BNF rules to
     */
    private static void writeBNF(String text, String path)
    {
        try
        {
            Files.write(Paths.get(path), text.getBytes());
        }
        catch (IOException e)
        {
            System.err.println("[ERROR] Failed to find file: " + path);
        }
    }
}
