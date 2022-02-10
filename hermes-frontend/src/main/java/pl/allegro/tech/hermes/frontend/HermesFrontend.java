package pl.allegro.tech.hermes.frontend;

@Deprecated
public final class HermesFrontend { //TODO - remove

//    private static final Logger logger = LoggerFactory.getLogger(HermesFrontend.class);

//    private final ServiceLocator serviceLocator;
//    private final HooksHandler hooksHandler;
//    private final List<Function<ServiceLocator, LogRepository>> logRepositories;
//    private final HermesServer hermesServer;
//    private final Trackers trackers;

//    public static void main(String[] args) throws Exception {
//        frontend().build().start();
//    }

//    private HermesFrontend(HooksHandler hooksHandler,
//                           List<Binder> binders,
//                           List<Function<ServiceLocator, LogRepository>> logRepositories,
//                           boolean flushLogsShutdownHookEnabled
//                           HermesServer hermesServer,
//                           Trackers trackers,
//                           ConfigFactory configFactory
//                           ) {
//        this.hooksHandler = hooksHandler;
//        this.logRepositories = logRepositories;
//        this.hermesServer = hermesServer;
//        this.trackers = trackers;

//        serviceLocator = createDIContainer(binders);

//        hermesServer = serviceLocator.getService(HermesServer.class);
//        trackers = serviceLocator.getService(Trackers.class);

//        ConfigFactory config = serviceLocator.getService(ConfigFactory.class);
//        if (configFactory.getBooleanProperty(FRONTEND_GRACEFUL_SHUTDOWN_ENABLED)) {
//            hooksHandler.addShutdownHook(gracefulShutdownHook());
//        }
//        if (configFactory.getBooleanProperty(FRONTEND_STARTUP_TOPIC_METADATA_LOADING_ENABLED)) {
//            hooksHandler.addBeforeStartHook(serviceLocator.getService(TopicMetadataLoadingStartupHook.class));//TODO: order
//        }
//        if (configFactory.getBooleanProperty(FRONTEND_STARTUP_TOPIC_SCHEMA_LOADING_ENABLED)) {
//            hooksHandler.addBeforeStartHook(serviceLocator.getService(TopicSchemaLoadingStartupHook.class));//TODO: order
////        }
//        hooksHandler.addStartupHook((s) -> s.getService(HealthCheckService.class).startup());
//        hooksHandler.addShutdownHook(defaultShutdownHook());
//        if (flushLogsShutdownHookEnabled) { //TODO: property
//            hooksHandler.addShutdownHook(new FlushLogsShutdownHook());
//        }
//        if (!configFactory.getBooleanProperty(FRONTEND_RESPONSE_ERROR_LOGGER_ENABLED)) {//TODO
//            LoggerConfiguration.disableResponseErrorLogger();
//        }
//    }

//    private ServiceAwareHook gracefulShutdownHook() {
//        return new AbstractShutdownHook() {
//            @Override
//            public void shutdown() throws InterruptedException {
//                hermesServer.gracefulShutdown();
//            }
//
//            @Override
//            public int getPriority() {
//                return Hook.HIGHER_PRIORITY;
//            }
//        };
//    }
//
//    private AbstractShutdownHook defaultShutdownHook() {
//        return new AbstractShutdownHook() {
//            @Override
//            public void shutdown() throws InterruptedException {
//                hermesServer.shutdown();
//                serviceLocator.shutdown();
//            }
//        };
//    }

//    public void start() {
//        logRepositories.forEach(serviceLocatorLogRepositoryFunction ->
//                trackers.add(serviceLocatorLogRepositoryFunction.apply(serviceLocator)));

//        serviceLocator.getService(PersistentBufferExtension.class).extend();
//        startCaches(serviceLocator);

//        hooksHandler.runBeforeStartHooks(serviceLocator);//TODO: order - before start hooks vs hermes server vs startup hooks
//        hermesServer.start();
//        hooksHandler.startup(serviceLocator);
//    }

//    private void startCaches(ServiceLocator locator) {
//        try {
//            locator.getService(ModelAwareZookeeperNotifyingCache.class).start();
//        } catch(Exception e) {
//            logger.error("Failed to startup Hermes Frontend", e);
//        }
//    }

//    public void stop() {
//        hooksHandler.shutdown(serviceLocator);
//    }

//    public <T> T getService(Class<T> clazz) {
//        return serviceLocator.getService(clazz);
//    }
//
//    public <T> T getService(Class<T> clazz, String name) {
//        return serviceLocator.getService(clazz, name);
//    }

//    private ServiceLocator createDIContainer(List<Binder> binders) {
//        String uniqueName = "HermesFrontendLocator" + UUID.randomUUID();
//
//        return ServiceLocatorUtilities.bind(uniqueName, binders.toArray(new Binder[binders.size()]));
//    }

//    public static Builder frontend() { //TODO
////        return new Builder();
//    }

//    public static final class Builder {

//        private static final int CUSTOM_BINDER_HIGH_PRIORITY = 10;

//        private final HooksHandler hooksHandler = new HooksHandler();
//        private final List<Binder> binders = Lists.newArrayList(
//                new CommonBinder(),
//                new FrontendBinder(hooksHandler)
//        );
//        private final BrokerListeners listeners = new BrokerListeners();
//        private final List<Function<ServiceLocator, LogRepository>> logRepositories = new ArrayList<>();
//        private boolean flushLogsShutdownHookEnabled = true;

//        public HermesFrontend build() {
//            withDefaultRankBinding(listeners, BrokerListeners.class);
//            binders.add(new TrackersBinder(new ArrayList<>()));
//            return new HermesFrontend(hooksHandler, binders, logRepositories, flushLogsShutdownHookEnabled);
//        }

//        public Builder withBeforeStartHook(ServiceAwareHook hook) {
//            hooksHandler.addBeforeStartHook(hook);
//            return this;
//        }
//
//        public Builder withBeforeStartHook(Hook hook) {
//            withBeforeStartHook(s -> hook.apply());
//            return this;
//        }

//        public Builder withStartupHook(ServiceAwareHook hook) {
//            hooksHandler.addStartupHook(hook);
//            return this;
//        }
//
//        public Builder withStartupHook(Hook hook) {
//            return withStartupHook(s -> hook.apply());
//        }

//        public Builder withShutdownHook(ServiceAwareHook hook) {
//            hooksHandler.addShutdownHook(hook);
//            return this;
//        }
//
//        public Builder withShutdownHook(Hook hook) {
//            return withShutdownHook(s -> hook.apply());
//        }


//        public Builder withDisabledGlobalShutdownHook() {//TODO
//            hooksHandler.disableGlobalShutdownHook();
//            return this;
//        }
//
//        public Builder withDisabledFlushLogsShutdownHook() {//TODO
//            flushLogsShutdownHookEnabled = false;
//            return this;
//        }

//        public Builder withBrokerTimeoutListener(BrokerTimeoutListener brokerTimeoutListener) {
//            listeners.addTimeoutListener(brokerTimeoutListener);
//            return this;
//        }
//
//        public Builder withBrokerAcknowledgeListener(BrokerAcknowledgeListener brokerAcknowledgeListener) {
//            listeners.addAcknowledgeListener(brokerAcknowledgeListener);
//            return this;
//        }
//
//        public Builder withBrokerErrorListener(BrokerErrorListener brokerErrorListener) {
//            listeners.addErrorListener(brokerErrorListener);
//            return this;
//        }
//
//        public Builder withLogRepository(Function<ServiceLocator, LogRepository> logRepository) {
//            logRepositories.add(logRepository);
//            return this;
//        }
//
//        public Builder withHeadersPropagator(HeadersPropagator headersPropagator) {
//            return withBinding(headersPropagator, HeadersPropagator.class);
//        }
//
//        public Builder withKafkaTopicsNamesMapper(KafkaNamesMapper kafkaNamesMapper) {
//            return withBinding(kafkaNamesMapper, KafkaNamesMapper.class);
//        }

//        public Builder withAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
//            return withBinding(authenticationConfiguration, AuthenticationConfiguration.class);
//        }

//        public Builder withSslContextFactory(SslContextFactory sslContextFactory) {
//            return withBinding(sslContextFactory, SslContextFactory.class);
//        }

//        public <T> Builder withBinding(T instance, Class<T> clazz) {
//            return withBinding(instance, clazz, clazz.getName());
//        }

//        public <T> Builder withBinding(T instance, Class<T> clazz, String name) {
//            binders.add(new AbstractBinder() {
//                @Override
//                protected void configure() {
//                    bind(instance).to(clazz).named(name).ranked(CUSTOM_BINDER_HIGH_PRIORITY);
//                }
//            });
//            return this;
//        }

//        private <T> Builder withDefaultRankBinding(T instance, Class<T> clazz) {
//            binders.add(new AbstractBinder() {
//                @Override
//                protected void configure() {
//                    bind(instance).to(clazz).named(clazz.getName());
//                }
//            });
//            return this;
//        }
//    }
}

