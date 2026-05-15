package io.omnimail.cms.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "omnimail")
public class OmnimailProperties {

    private final System system = new System();
    private final Tenant tenant = new Tenant();
    private final Storage storage = new Storage();
    private final Gcs gcs = new Gcs();
    private final Email email = new Email();

    public System getSystem() {
        return system;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Storage getStorage() {
        return storage;
    }

    public Gcs getGcs() {
        return gcs;
    }

    public Email getEmail() {
        return email;
    }

    public static class System {
        private String environment = "dev";
        private final Git git = new Git();

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public Git getGit() {
            return git;
        }
    }

    public static class Git {
        private final Author author = new Author();
        private final Author committer = new Author();
        private final Repository repository = new Repository();

        public Author getAuthor() {
            return author;
        }

        public Author getCommitter() {
            return committer;
        }

        public Repository getRepository() {
            return repository;
        }
    }

    public static class Author {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Repository {
        private String defaultBranch = "main";
        private String templatesPath = "templates";

        public String getDefaultBranch() {
            return defaultBranch;
        }

        public void setDefaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
        }

        public String getTemplatesPath() {
            return templatesPath;
        }

        public void setTemplatesPath(String templatesPath) {
            this.templatesPath = templatesPath;
        }
    }

    public static class Tenant {
        private String defaultId = "default";

        public String getDefaultId() {
            return defaultId;
        }

        public void setDefaultId(String defaultId) {
            this.defaultId = defaultId;
        }
    }

    public static class Storage {
        private String type = "local";
        private String localPath = "./data/blobs";
        private String cdnBaseUrl = "http://localhost:8080";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }

        public String getCdnBaseUrl() {
            return cdnBaseUrl;
        }

        public void setCdnBaseUrl(String cdnBaseUrl) {
            this.cdnBaseUrl = cdnBaseUrl;
        }
    }

    public static class Gcs {
        private String projectId = "";
        private String bucket = "omnimail-assets";

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }

    public static class Email {
        private String provider = "logging";

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }
    }
}
