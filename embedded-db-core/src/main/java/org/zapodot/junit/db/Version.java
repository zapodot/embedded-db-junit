package org.zapodot.junit.db;

import java.io.IOException;
import java.util.Properties;

public class Version {

    private static final String PROP_FILE_BUILD_INFO = "build.info.properties";

    private static final String PROP_FILE_GIT_PROPERTIES = "git.properties";

    private final String groupId;

    private final String artifactId;

    private final String projectVersion;

    private final String gitBranch;

    private final String gitCommit;

    public Version() {
        this(loadBuildInfoAndGitProperties());
    }

    private Version(Properties properties) {
        this(properties.getProperty("project.groupId"),
             properties.getProperty("project.artifactId"),
             properties.getProperty("project.version"),
             properties.getProperty("git.branch"),
             properties.getProperty("git.commit.id"));
    }

    private Version(final String groupId,
                    final String artifactId,
                    final String projectVersion,
                    final String gitBranch,
                    final String gitCommit) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.projectVersion = projectVersion;
        this.gitBranch = gitBranch;
        this.gitCommit = gitCommit;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return projectVersion;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    private static Properties loadBuildInfoAndGitProperties() {
        final Properties properties = loadBuildInfo();
        properties.putAll(loadGitProperties());
        return properties;
    }

    private static Properties loadBuildInfo() {
        return loadPropertiesFile(PROP_FILE_BUILD_INFO);
    }

    private static Properties loadGitProperties() {
        return loadPropertiesFile(PROP_FILE_GIT_PROPERTIES);
    }

    private static Properties loadPropertiesFile(final String file) {
        final Properties properties = new Properties();
        try {
            properties.load(Version.class.getResourceAsStream("/" + file));
        } catch (IOException e) {
            throw new IllegalStateException("Could not load build.info.properties", e);
        }
        return properties;
    }
}
