package org.kolulu;


import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.help.Help;
import org.kolulu.cli.FlatCommand;
import org.kolulu.cli.MetricCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author kolulu
 * <br/>Created at 2022/1/8 15:47
 */
@com.github.rvesse.airline.annotations.Cli(name = "json2csv-cli",
        description = "A simple tool that converts flat json to csv format. A domain specific json example included.",
        defaultCommand = Help.class,
        commands = {Help.class, MetricCommand.class, FlatCommand.class})
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Cli<Runnable> cli = new Cli<>(Main.class);
        Runnable cmd = cli.parse(args);
        cmd.run();
    }
}
