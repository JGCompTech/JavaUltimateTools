package com.jgcomptech.tools.authc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * An object representing a user login session.
 * @since 1.4.0
 * @since 1.5.0 Updated to add open time and duration
 */
public final class Session {
    private final String username;
    private final UserRole userRole;
    private final LocalDateTime openTime;

    /**
     * Creates an instance of a session.
     * @param username the username of the logged in user
     * @param userRole the user role of the logged in user
     */
    public Session(final String username, final UserRole userRole) {
        this.username = username;
        this.userRole = userRole;
        this.openTime = LocalDateTime.now();
    }

    /**
     * Returns the username of the logged in user.
     * @return the username of the logged in user
     */
    public String getUsername() { return username; }

    /**
     * Returns the user role of the logged in user.
     * @return the user role of the logged in user
     */
    public UserRole getUserRole() { return userRole; }

    /**
     * Returns a LocalDateTime object containing the timestamp when the session was opened.
     * @return a LocalDateTime object containing the timestamp when the session was opened
     * @since 1.5.0
     */
    public LocalDateTime getOpenTime() { return openTime; }

    /**
     * Returns a Duration object containing the elapsed time the session has been opened.
     * @return a Duration object containing the elapsed time the session has been opened
     * @since 1.5.0
     */
    public Duration getDuration() {
        return Duration.between(openTime, LocalDateTime.now());
    }

    /**
     * Returns a string of the elapsed time the session has been opened in the format 00:00:00.
     * @return a string of the elapsed time the session has been opened in the format 00:00:00
     * @since 1.5.0
     */
    public String getDurationString() {
        final var millis = getDuration().toMillis();

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    /**
     * Returns a string of the elapsed time the session has been opened in the format 00:00:00.0000.
     * @return a string of the elapsed time the session has been opened in the format 00:00:00.0000
     * @since 1.5.0
     */
    public String getDurationStringLong() {
        final var millis = getDuration().toMillis();

        return String.format("%02d:%02d:%02d.%04d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
    }

    /**
     * Returns a string of the elapsed time the session has been opened in sentence format.
     * @return a string of the elapsed time the session has been opened in sentence format
     * @since 1.5.0
     */
    public String getDurationFullString() {
        final var toDateTime = LocalDateTime.now();

        var tempDateTime = LocalDateTime.from(openTime);

        final var years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        final var months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        final var days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);


        final var hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        final var minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        final var seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);
        tempDateTime = tempDateTime.plusSeconds(seconds);

        final var millis = tempDateTime.until(toDateTime, ChronoUnit.MILLIS);

        return years + " years " +
                months + " months " +
                days + " days " +
                hours + " hours " +
                minutes + " minutes " +
                seconds + " seconds " +
                millis + " milliseconds.";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Session)) return false;

        final var session = (Session) o;

        return new EqualsBuilder()
                .append(username, session.username)
                .append(userRole, session.userRole)
                .append(openTime, session.openTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(username)
                .append(userRole)
                .append(openTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("userRole", userRole)
                .append("openTime", openTime)
                .toString();
    }
}
