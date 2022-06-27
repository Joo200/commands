/*
 * Copyright (c) 2016-2018 Daniel Ennis (Aikar) - MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package co.aikar.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHelpFormatter {

    private final CommandManager<?,?,?> manager;

    public CommandHelpFormatter(CommandManager<?,?,?> manager) {
        this.manager = manager;
    }


    public void showAllResults(CommandHelp commandHelp, List<HelpEntry> entries) {
        CommandIssuer issuer = commandHelp.getIssuer();
        printHelpHeader(commandHelp, issuer);
        for (HelpEntry e : entries) {
            printHelpCommand(commandHelp, issuer, e);
        }
        printHelpFooter(commandHelp, issuer);
    }

    public void showSearchResults(CommandHelp commandHelp, List<HelpEntry> entries) {
        CommandIssuer issuer = commandHelp.getIssuer();
        printSearchHeader(commandHelp, issuer);
        for (HelpEntry e : entries) {
            printSearchEntry(commandHelp, issuer, e);
        }
        printSearchFooter(commandHelp, issuer);
    }

    public void showDetailedHelp(CommandHelp commandHelp, HelpEntry entry) {
        CommandIssuer issuer = commandHelp.getIssuer();

        // normal help line
        printDetailedHelpCommand(commandHelp, issuer, entry);

        // additionally detailed help for params
        for (CommandParameter param : entry.getParameters()) {
            String description = param.getDescription();
            if (description != null && !description.isEmpty()) {
                printDetailedParameter(commandHelp, issuer, entry, param);
            }
        }
    }

    // ########
    // # help #
    // ########

    public void printHelpHeader(CommandHelp help, CommandIssuer issuer) {
        issuer.sendMessage(MessageType.HELP, MessageKeys.HELP_HEADER, getHeaderFooterFormatReplacements(help));
    }

    public void printHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        this.manager.sendMessage(issuer, MessageType.HELP, MessageKeys.HELP_FORMAT, getEntryFormatReplacements(help, entry));
    }

    public void printHelpFooter(CommandHelp help, CommandIssuer issuer) {
        if (help.isOnlyPage()) {
            return;
        }
        issuer.sendMessage(MessageType.HELP, MessageKeys.HELP_PAGE_INFORMATION, getHeaderFooterFormatReplacements(help));
    }

    // ##########
    // # search #
    // ##########

    public void printSearchHeader(CommandHelp help, CommandIssuer issuer) {
        issuer.sendMessage(MessageType.HELP, MessageKeys.HELP_SEARCH_HEADER, getHeaderFooterFormatReplacements(help));
    }

    public void printSearchEntry(CommandHelp help, CommandIssuer issuer, HelpEntry page) {
        this.manager.sendMessage(issuer, MessageType.HELP, MessageKeys.HELP_FORMAT, getEntryFormatReplacements(help, page));
    }

    public void printSearchFooter(CommandHelp help, CommandIssuer issuer) {
        if (help.isOnlyPage()) {
            return;
        }
        issuer.sendMessage(MessageType.HELP, MessageKeys.HELP_PAGE_INFORMATION, getHeaderFooterFormatReplacements(help));
    }


    // ############
    // # detailed #
    // ############

    public void printDetailedHelpHeader(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        issuer.sendMessage(MessageType.HELP, MessageKeys.HELP_DETAILED_HEADER,
                Placeholder.parsed("command", entry.getCommand()),
                Placeholder.parsed("commandprefix", help.getCommandPrefix())
        );
    }


    public void printDetailedHelpCommand(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        this.manager.sendMessage(issuer, MessageType.HELP, MessageKeys.HELP_DETAILED_COMMAND_FORMAT, getEntryFormatReplacements(help, entry));
    }

    public void printDetailedParameter(CommandHelp help, CommandIssuer issuer, HelpEntry entry, CommandParameter param) {
        this.manager.sendMessage(issuer, MessageType.HELP, MessageKeys.HELP_DETAILED_PARAMETER_FORMAT, getParameterFormatReplacements(help, param, entry));
    }

    public void printDetailedHelpFooter(CommandHelp help, CommandIssuer issuer, HelpEntry entry) {
        // default doesn't have a footer
    }

    /**
     * Override this to control replacements
     *
     * @param help
     * @return
     */
    public TagResolver[] getHeaderFooterFormatReplacements(CommandHelp help) {
        return new TagResolver[]{
                Placeholder.parsed("search", help.search != null ? String.join(" ", help.search) : ""),
                Placeholder.parsed("command", help.getCommandName()),
                Placeholder.parsed("commandprefix", help.getCommandPrefix()),
                Placeholder.parsed("rootcommand", help.getCommandName()),
                Placeholder.parsed("page", "" + help.getPage()),
                Placeholder.parsed("totalpages", "" + help.getTotalPages()),
                Placeholder.parsed("results", "" + help.getTotalResults())
        };
    }

    /**
     * Override this to control replacements
     *
     * @param help
     * @param entry
     * @return
     */
    public TagResolver[] getEntryFormatReplacements(CommandHelp help, HelpEntry entry) {
        //{command} {parameters} {separator} {description}
        return new TagResolver[]{
                Placeholder.parsed("command", entry.getCommand()),
                Placeholder.parsed("commandprefix", help.getCommandPrefix()),
                Placeholder.parsed("parameters", entry.getParameterSyntax(help.getIssuer())),
                Placeholder.parsed("separator", entry.getDescription().isEmpty() ? "" : "-"),
                Placeholder.parsed("description", entry.getDescription())
        };
    }

    /**
     * Override this to control replacements
     *
     * @param help
     * @param param
     * @param entry
     * @return
     */
    @NotNull
    public TagResolver[] getParameterFormatReplacements(CommandHelp help, CommandParameter param, HelpEntry entry) {
        //{name} {description}
        return new TagResolver[]{
                Placeholder.parsed("name", param.getDisplayName(help.getIssuer())),
                Placeholder.parsed("syntaxorname", ACFUtil.nullDefault(param.getSyntax(help.getIssuer()), param.getDisplayName(help.getIssuer()))),
                Placeholder.parsed("syntax", ACFUtil.nullDefault(param.getSyntax(help.getIssuer()), "")),
                Placeholder.parsed("description", ACFUtil.nullDefault(param.getDescription(), "")),
                Placeholder.parsed("command", help.getCommandName()),
                Placeholder.parsed("fullcommand", entry.getCommand()),
                Placeholder.parsed("commandprefix", help.getCommandPrefix())
        };
    }
}
