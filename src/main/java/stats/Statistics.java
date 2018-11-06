package stats;

public class Statistics {
    private int uniqueUsersConnected;
    private double averageUserSessionDuration;
    private static int totalUniqueUsers;

    public Statistics(int uniqueUsersConnected, double averageUserSessionDuration, int totalUniqueUsers) {
        this.uniqueUsersConnected = uniqueUsersConnected;
        this.averageUserSessionDuration = averageUserSessionDuration;
        Statistics.totalUniqueUsers = totalUniqueUsers;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "uniqueUsersConnected=" + uniqueUsersConnected +
                ", averageUserSessionDuration=" + averageUserSessionDuration +
                ", totalUniqueUsers=" + totalUniqueUsers +
                '}';
    }
}
