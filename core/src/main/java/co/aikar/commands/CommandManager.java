/*
 * Copyright (c) 2016-2017 Daniel Ennis (Aikar) - MIT License
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

import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.format.LangPlaceholder;
import co.aikar.commands.format.MessageFormatter;
import co.aikar.locales.MessageKeyProvider;
import co.aikar.util.Table;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;


@SuppressWarnings("WeakerAccess")
public abstract class CommandManager<
        IT,
        CEC extends CommandExecutionContext,
        CC extends ConditionContext
        > {

    /**
     * This is a stack incase a command calls a command
     */
    static ThreadLocal<Stack<CommandOperationContext>> commandOperationContext = ThreadLocal.withInitial(() -> new Stack<CommandOperationContext>() {
        @Override
        public synchronized CommandOperationContext peek() {
            return super.size() == 0 ? null : super.peek();
        }
    });
    protected Map<String, RootCommand> rootCommands = new HashMap<>();
    protected final CommandReplacements replacements = new CommandReplacements(this);
    protected final CommandConditions<CEC, CC> conditions = new CommandConditions<>(this);
    protected ExceptionHandler defaultExceptionHandler = null;
    boolean logUnhandledExceptions = true;
    protected Table<Class<?>, String, Object> dependencies = new Table<>();
    protected CommandHelpFormatter helpFormatter = new CommandHelpFormatter(this);

    protected boolean usePerIssuerLocale = false;
    protected List<IssuerLocaleChangedCallback> localeChangedCallbacks = new ArrayList<>();
    protected Set<Locale> supportedLanguages = new HashSet<>(Arrays.asList(Locales.ENGLISH, Locales.DUTCH, Locales.GERMAN, Locales.SPANISH, Locales.FRENCH, Locales.CZECH, Locales.PORTUGUESE, Locales.SWEDISH, Locales.NORWEGIAN_BOKMAAL, Locales.NORWEGIAN_NYNORSK, Locales.RUSSIAN, Locales.BULGARIAN, Locales.HUNGARIAN, Locales.TURKISH, Locales.JAPANESE, Locales.CHINESE, Locales.SIMPLIFIED_CHINESE, Locales.TRADITIONAL_CHINESE));

    protected int defaultHelpPerPage = 10;

    protected MiniMessage miniMessage;

    protected Map<UUID, Locale> issuersLocale = new ConcurrentHashMap<>();

    private final Set<String> unstableAPIs = new HashSet<>();

    private final Annotations annotations = new Annotations(this);
    private final CommandRouter router = new CommandRouter(this);

    private TagResolver defaultFormatter;
    private final Map<MessageType, TagResolver> formatters = new HashMap<>();

    public CommandManager() {
        defaultFormatter = MessageFormatter.DEFAULT;
        formatters.put(MessageType.INFO, MessageFormatter.INFO_REPLACEMENTS);
        formatters.put(MessageType.ERROR, MessageFormatter.ERROR_REPLACEMENTS);
        formatters.put(MessageType.SYNTAX, MessageFormatter.SYNTAX_REPLACEMENTS);
        formatters.put(MessageType.HELP, MessageFormatter.HELP_REPLACEMENTS);

        miniMessage = MiniMessage.miniMessage();
    }

    public static CommandOperationContext getCurrentCommandOperationContext() {
        return commandOperationContext.get().peek();
    }

    public static CommandIssuer getCurrentCommandIssuer() {
        CommandOperationContext context = commandOperationContext.get().peek();
        return context != null ? context.getCommandIssuer() : null;
    }

    public static CommandManager getCurrentCommandManager() {
        CommandOperationContext context = commandOperationContext.get().peek();
        return context != null ? context.getCommandManager() : null;
    }

    public void setFormat(MessageType type, TagResolver formatter) {
        formatters.put(type, formatter);
    }

    public TagResolver getFormat(MessageType type) {
        return formatters.getOrDefault(type, defaultFormatter);
    }

    public TagResolver getDefaultFormatter() {
        return defaultFormatter;
    }

    public void setDefaultFormatter(TagResolver defaultFormatter) {
        this.defaultFormatter = defaultFormatter;
    }

    public CommandConditions<CEC, CC> getCommandConditions() {
        return conditions;
    }

    /**
     * Gets the command contexts manager
     *
     * @return Command Contexts
     */
    public abstract CommandContexts<?> getCommandContexts();

    /**
     * Gets the command completions manager
     *
     * @return Command Completions
     */
    public abstract CommandCompletions<?> getCommandCompletions();

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public CommandHelp generateCommandHelp(@NotNull String command) {
        verifyUnstableAPI("help");
        CommandOperationContext context = getCurrentCommandOperationContext();
        if (context == null) {
            throw new IllegalStateException("This method can only be called as part of a command execution.");
        }
        return generateCommandHelp(context.getCommandIssuer(), command);
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public CommandHelp generateCommandHelp(CommandIssuer issuer, @NotNull String command) {
        verifyUnstableAPI("help");
        return generateCommandHelp(issuer, obtainRootCommand(command));
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public CommandHelp generateCommandHelp() {
        verifyUnstableAPI("help");
        CommandOperationContext context = getCurrentCommandOperationContext();
        if (context == null) {
            throw new IllegalStateException("This method can only be called as part of a command execution.");
        }
        String commandLabel = context.getCommandLabel();
        return generateCommandHelp(context.getCommandIssuer(), this.obtainRootCommand(commandLabel));
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public CommandHelp generateCommandHelp(CommandIssuer issuer, RootCommand rootCommand) {
        verifyUnstableAPI("help");
        return new CommandHelp(this, rootCommand, issuer);
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public int getDefaultHelpPerPage() {
        verifyUnstableAPI("help");
        return defaultHelpPerPage;
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public void setDefaultHelpPerPage(int defaultHelpPerPage) {
        verifyUnstableAPI("help");
        this.defaultHelpPerPage = defaultHelpPerPage;
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public void setHelpFormatter(CommandHelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }

    /**
     * @deprecated Unstable API
     */
    @Deprecated
    @UnstableAPI
    public CommandHelpFormatter getHelpFormatter() {
        return helpFormatter;
    }

    public CommandRouter getRouter() {
        return router;
    }

    /**
     * Registers a command with ACF
     *
     * @param command The command to register
     * @return boolean
     */
    public abstract void registerCommand(BaseCommand command);

    public abstract boolean hasRegisteredCommands();

    public abstract boolean isCommandIssuer(Class<?> type);

    public abstract CommandIssuer getCommandIssuer(Object issuer);

    public abstract RootCommand createRootCommand(String cmd);

    /**
     * Returns a Locales Manager to add and modify language tables for your commands.
     *
     * @return
     */
    public abstract Locales getLocales();

    public boolean usingPerIssuerLocale() {
        return usePerIssuerLocale;
    }

    public boolean usePerIssuerLocale(boolean setting) {
        boolean old = usePerIssuerLocale;
        usePerIssuerLocale = setting;
        return old;
    }

    public ConditionContext createConditionContext(CommandIssuer issuer, String config) {
        //noinspection unchecked
        return new ConditionContext(issuer, config);
    }

    public abstract CommandExecutionContext createCommandContext(RegisteredCommand command, CommandParameter parameter, CommandIssuer sender, List<String> args, int i, Map<String, Object> passedArgs);

    public abstract CommandCompletionContext createCompletionContext(RegisteredCommand command, CommandIssuer sender, String input, String config, String[] args);

    public abstract void log(final LogLevel level, final String message, final Throwable throwable);

    public void log(final LogLevel level, final String message) {
        log(level, message, null);
    }

    /**
     * Lets you add custom string replacements that can be applied to annotation values,
     * to reduce duplication/repetition of common values such as permission nodes and command prefixes.
     * <p>
     * Any replacement registered starts with a %
     * <p>
     * So for ex @CommandPermission("%staff")
     *
     * @return Replacements Manager
     */
    public CommandReplacements getCommandReplacements() {
        return replacements;
    }

    public boolean hasPermission(CommandIssuer issuer, Set<String> permissions) {
        for (String permission : permissions) {
            if (!hasPermission(issuer, permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPermission(CommandIssuer issuer, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        for (String perm : ACFPatterns.COMMA.split(permission)) {
            if (!perm.isEmpty() && !issuer.hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }

    public synchronized RootCommand getRootCommand(@NotNull String cmd) {
        return rootCommands.get(ACFPatterns.SPACE.split(cmd.toLowerCase(Locale.ENGLISH), 2)[0]);
    }

    public synchronized RootCommand obtainRootCommand(@NotNull String cmd) {
        return rootCommands.computeIfAbsent(ACFPatterns.SPACE.split(cmd.toLowerCase(Locale.ENGLISH), 2)[0], this::createRootCommand);
    }

    public abstract Collection<RootCommand> getRegisteredRootCommands();

    public RegisteredCommand createRegisteredCommand(BaseCommand command, String cmdName, Method method, String prefSubCommand) {
        return new RegisteredCommand(command, cmdName, method, prefSubCommand);
    }

    /**
     * Sets the default {@link ExceptionHandler} that is called when an exception occurs while executing a command, if the command doesn't have its own exception handler registered.
     *
     * @param exceptionHandler the handler that should handle uncaught exceptions.  May not be null if logExceptions is false
     */
    public void setDefaultExceptionHandler(ExceptionHandler exceptionHandler) {
        if (exceptionHandler == null && !this.logUnhandledExceptions) {
            throw new IllegalArgumentException("You may not disable the default exception handler and have logging of unhandled exceptions disabled");
        }
        defaultExceptionHandler = exceptionHandler;
    }

    /**
     * Sets the default {@link ExceptionHandler} that is called when an exception occurs while executing a command, if the command doesn't have its own exception handler registered, and lets you control if ACF should also log the exception still.
     * <p>
     * If you disable logging, you need to log it yourself in your handler.
     *
     * @param exceptionHandler the handler that should handle uncaught exceptions. May not be null if logExceptions is false
     * @param logExceptions    Whether or not to log exceptions.
     */
    public void setDefaultExceptionHandler(ExceptionHandler exceptionHandler, boolean logExceptions) {
        if (exceptionHandler == null && !logExceptions) {
            throw new IllegalArgumentException("You may not disable the default exception handler and have logging of unhandled exceptions disabled");
        }
        this.logUnhandledExceptions = logExceptions;
        this.defaultExceptionHandler = exceptionHandler;
    }

    public boolean isLoggingUnhandledExceptions() {
        return this.logUnhandledExceptions;
    }

    /**
     * Gets the current default exception handler, might be null.
     *
     * @return the default exception handler
     */
    public ExceptionHandler getDefaultExceptionHandler() {
        return defaultExceptionHandler;
    }

    protected boolean handleUncaughtException(BaseCommand scope, RegisteredCommand registeredCommand, CommandIssuer sender, List<String> args, Throwable t) {
        if (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        boolean result = false;
        if (scope.getExceptionHandler() != null) {
            result = scope.getExceptionHandler().execute(scope, registeredCommand, sender, args, t);
        } else if (defaultExceptionHandler != null) {
            result = defaultExceptionHandler.execute(scope, registeredCommand, sender, args, t);
        }
        return result;
    }

    public abstract Collection<? extends IT> getAllPlayers();
    public void broadcastMessage(MessageKeyProvider key) {
        getAllPlayers().forEach(it -> sendMessage(it, key));
    }

    public void broadcastMessage(MessageKeyProvider key, TagResolver... resolvers) {
        getAllPlayers().forEach(it -> sendMessage(it, key, resolvers));
    }

    public void sendMessage(IT issuerArg, MessageKeyProvider key) {
        sendMessage(getCommandIssuer(issuerArg), MessageType.INFO, key);
    }

    public void sendMessage(IT issuerArg, MessageType type, MessageKeyProvider key) {
        sendMessage(getCommandIssuer(issuerArg), type, key);
    }

    public void sendMessage(CommandIssuer issuer, MessageKeyProvider key) {
        sendMessage(issuer, MessageType.INFO, key);
    }

    public void sendMessage(CommandIssuer issuer, MessageType type, MessageKeyProvider key) {
        issuer.sendMessage(formatMessage(issuer, type, key));
    }

    public void sendMessage(IT issuerArg, MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(getCommandIssuer(issuerArg), MessageType.INFO, key, replacements);
    }

    public void sendMessage(IT issuerArg, MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(getCommandIssuer(issuerArg), type, key, replacements);
    }

    public void sendMessage(CommandIssuer issuer, MessageKeyProvider key, TagResolver... replacements) {
        sendMessage(issuer, MessageType.INFO, key, replacements);
    }

    public void sendMessage(CommandIssuer issuer, MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        issuer.sendMessage(formatMessage(issuer, type, key, replacements));
    }

    public Component formatMessage(Locale locale, MessageType type, MessageKeyProvider key) {
        TagResolver tags = TagResolver.builder()
                .resolver(formatters.getOrDefault(type, defaultFormatter))
                .resolver(new LangPlaceholder(this, locale)).build();

        return miniMessage.deserialize("<c1>" + getMessage(locale, key), tags);
    }

    public Component formatMessage(Locale locale, MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        TagResolver tags = TagResolver.builder()
                .resolver(formatters.getOrDefault(type, defaultFormatter))
                .resolver(new LangPlaceholder(this, locale))
                .resolvers(replacements).build();

        return miniMessage.deserialize("<c1>" + getMessage(locale, key), tags);
    }

    public Component formatMessage(IT issuerArg, MessageType type, MessageKeyProvider key) {
        return formatMessage(getCommandIssuer(issuerArg), type, key);
    }

    public Component formatMessage(CommandIssuer issuer, MessageType type, MessageKeyProvider key) {
        TagResolver tags = TagResolver.builder()
                .resolver(formatters.getOrDefault(type, defaultFormatter))
                .resolver(new LangPlaceholder(this, issuer)).build();

        return miniMessage.deserialize("<c1>" + getMessage(issuer, key), tags);
    }

    public Component formatMessage(IT issuerArg, MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        return formatMessage(getCommandIssuer(issuerArg), type, key, replacements);
    }

    public Component formatMessage(CommandIssuer issuer, MessageType type, MessageKeyProvider key, TagResolver... replacements) {
        TagResolver tags = TagResolver.builder()
                .resolver(formatters.getOrDefault(type, defaultFormatter))
                .resolver(new LangPlaceholder(this, issuer))
                .resolvers(replacements).build();

        return miniMessage.deserialize("<c1>" + getMessage(issuer, key), tags);
    }

    public String getMessage(CommandIssuer issuer, MessageKeyProvider key) {
        return getLocales().getMessage(issuer, key.getMessageKey());
    }

    public String getMessage(Locale locale, MessageKeyProvider key) {
        return getLocales().getMessage(locale, key.getMessageKey());
    }

    public void onLocaleChange(IssuerLocaleChangedCallback onChange) {
        localeChangedCallbacks.add(onChange);
    }

    public void notifyLocaleChange(CommandIssuer issuer, Locale oldLocale, Locale newLocale) {
        localeChangedCallbacks.forEach(cb -> {
            try {
                cb.onIssuerLocaleChange(issuer, oldLocale, newLocale);
            } catch (Exception e) {
                this.log(LogLevel.ERROR, "Error in notifyLocaleChange", e);
            }
        });
    }

    public Locale setIssuerLocale(IT issuer, Locale locale) {
        CommandIssuer commandIssuer = getCommandIssuer(issuer);

        Locale old = issuersLocale.put(commandIssuer.getUniqueId(), locale);
        if (!Objects.equals(old, locale)) {
            this.notifyLocaleChange(commandIssuer, old, locale);
        }

        return old;
    }

    public Locale getIssuerLocale(CommandIssuer issuer) {
        if (usingPerIssuerLocale() && issuer != null) {
            Locale locale = issuersLocale.get(issuer.getUniqueId());
            if (locale != null) {
                return locale;
            }
        }

        return getLocales().getDefaultLocale();
    }

    CommandOperationContext createCommandOperationContext(BaseCommand command, CommandIssuer issuer, String commandLabel, String[] args, boolean isAsync) {
        //noinspection unchecked
        return new CommandOperationContext(
                this,
                issuer,
                command,
                commandLabel,
                args,
                isAsync
        );
    }

    /**
     * Gets a list of all currently supported languages for this manager.
     * These locales will be automatically loaded from
     *
     * @return
     */
    public Set<Locale> getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * Adds a new locale to the list of automatic Locales to load Message Bundles for.
     * All bundles loaded under the previous supported languages will now automatically load for this new locale too.
     *
     * @param locale
     */
    public void addSupportedLanguage(Locale locale) {
        supportedLanguages.add(locale);
        getLocales().loadMissingBundles();
    }

    /**
     * Registers an instance of a class to be registered as an injectable dependency.<br>
     * The command manager will attempt to inject all fields in a command class that are annotated with
     * {@link co.aikar.commands.annotation.Dependency} with the provided instance.
     *
     * @param clazz    the class the injector should look for when injecting
     * @param instance the instance of the class that should be injected
     * @throws IllegalStateException when there is already an instance for the provided class registered
     */
    public <T> void registerDependency(Class<? extends T> clazz, T instance) {
        registerDependency(clazz, clazz.getName(), instance);
    }

    /**
     * Registers an instance of a class to be registered as an injectable dependency.<br>
     * The command manager will attempt to inject all fields in a command class that are annotated with
     * {@link co.aikar.commands.annotation.Dependency} with the provided instance.
     *
     * @param clazz    the class the injector should look for when injecting
     * @param key      the key which needs to be present if that
     * @param instance the instance of the class that should be injected
     * @throws IllegalStateException when there is already an instance for the provided class registered
     */
    public <T> void registerDependency(Class<? extends T> clazz, String key, T instance) {
        if (dependencies.containsKey(clazz, key)) {
            throw new IllegalStateException("There is already an instance of " + clazz.getName() + " with the key " + key + " registered!");
        }

        dependencies.put(clazz, key, instance);
    }

    /**
     * Attempts to inject instances of classes registered with {@link CommandManager#registerDependency(Class, Object)}
     * into all fields of the class and its superclasses that are marked with {@link Dependency}.
     *
     * @param baseCommand the instance which fields should be filled
     */
    void injectDependencies(BaseCommand baseCommand) {
        Class<?> clazz = baseCommand.getClass();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (annotations.hasAnnotation(field, Dependency.class)) {
                    String dependency = annotations.getAnnotationValue(field, Dependency.class);
                    String key = (key = dependency).isEmpty() ? field.getType().getName() : key;
                    Object object = dependencies.row(field.getType()).get(key);
                    if (object == null) {
                        throw new UnresolvedDependencyException("Could not find a registered instance of " +
                                field.getType().getName() + " with key " + key + " for field " + field.getName() +
                                " in class " + baseCommand.getClass().getName());
                    }

                    try {
                        boolean accessible = field.isAccessible();
                        if (!accessible) {
                            field.setAccessible(true);
                        }
                        field.set(baseCommand, object);
                        field.setAccessible(accessible);
                    } catch (IllegalAccessException e) {
                        log(LogLevel.ERROR, "Unable to inject dependency in class " + baseCommand.getClass().getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (!clazz.equals(BaseCommand.class));
    }

    /**
     * @deprecated Use this with caution! If you enable and use Unstable API's, your next compile using ACF
     * may require you to update your implementation to those unstable API's
     */
    @Deprecated
    public void enableUnstableAPI(String api) {
        unstableAPIs.add(api);
    }

    void verifyUnstableAPI(String api) {
        if (!unstableAPIs.contains(api)) {
            throw new IllegalStateException("Using an unstable API that has not been enabled ( " + api + "). See https://acfunstable.emc.gs");
        }
    }

    boolean hasUnstableAPI(String api) {
        return unstableAPIs.contains(api);
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    public String getCommandPrefix(CommandIssuer issuer) {
        return "";
    }

    public void showInvalidRuntime(IT sender, InvalidRuntimeException invalidCommandArg) {
        showInvalidRuntime(getCommandIssuer(sender), invalidCommandArg);
    }

    public void showInvalidRuntime(CommandIssuer sender, InvalidRuntimeException invalidCommandArg) {
        if (invalidCommandArg.getKey() != null) {
            sender.sendMessage(MessageType.ERROR, invalidCommandArg.key, invalidCommandArg.replacements);
        } else if (invalidCommandArg.getMessage() != null && !invalidCommandArg.getMessage().isEmpty()) {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_PREFIX, Placeholder.parsed("message", invalidCommandArg.getMessage()));
        } else {
            log(LogLevel.INFO, "Unable to show exception to player " + sender.getUsername() + ": " + invalidCommandArg);
        }
    }
}
