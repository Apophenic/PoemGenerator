# Approach
I immediately saw two ways to approach this problem. The first and most obvious was to parse the rules into a
hashmap, and store called rules in a stack. This probably would have worked fine (despite possible overcomplexity,
I'd also need to figure out _how_ to store a "rule" in the hashmap'), but it was breaking one of my cardinal rules:
never reinvent the wheel unless it's _absolutely necessary_ (which is maybe 5% of the time).

Having worked with BNF grammar in the past, I knew just enough about parsing grammars to realize this ruleset could
easily be turned into a workable grammar. The problem was that grammars are typically used when processing input. What
was my input going to be? I was working backwards from what I've previously done. I needed to generate arbitrary grammar
from rules _rather than apply grammar rules to arbitrary input_.

After a bit of brainstorming, I was able to see the problem from this new perspective. What if I could just generate
tests from a grammar file? That turned out to be the correct question to ask. After a little research, I stumbled
upon [gramtest](https://github.com/codelion/gramtest), which promised to do just that. It was so close to what I
needed: it's only shortcoming was that it generated every possible combination of the grammar, in order. Therefore, I
would end up with a poem that was 5 of the same line, with only the last few words changed. I forked gramtest, dove
into the code, and a few hours later had figured out what modifications I needed to make (literally 3 lines) to get
gramtest to serve my purpose. See the changes [here](https://github.com/Apophenic/gramtest).

Once that was squared away, all that was needed was to create a basic program that converted the rules list into a BNF
grammar formatted file and interfaced with my custom gramtest branch. That took a whooping 30 minutes to accomplish.

Compared to the former approach, this seemed like it would be the most scalable. You have an enormous (potentially
too enormous) amount of potential for working with the grammar because the rules have been parsed with ANTLR, rather
than just having them sitting haphazardly in a hashmap.

The only change I would consider making would be in how I handle parsing the tokens in #generateBNF(). I took the
"easy way" out by using \s and \n as the delimiter and used basic string manipulation. An arguably more thorough way
to convert the file would be recognizing tokens by using regular expressions. I decided against this, as having only
one example rules.txt made it impossible to extrapolate good regexps (and would have been overkill).

# Usage
Download the project and execute __run.bat__.

Command line operation:
~~~ shell
java -jar "../PoemGenerator.jar" -in "../rules.txt"
~~~

That's it!
