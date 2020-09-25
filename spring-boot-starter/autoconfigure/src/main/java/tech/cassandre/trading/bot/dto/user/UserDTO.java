package tech.cassandre.trading.bot.dto.user;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO representing user information.
 */
public final class UserDTO {

    /** User ID (usually username). */
    private final String id;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

    /** Information timestamp. */
    private final ZonedDateTime timestamp;

    /**
     * Constructor.
     *
     * @param builder Builder.
     */
    protected UserDTO(final UserDTO.Builder builder) {
        this.id = builder.id;
        if (builder.accounts != null) {
            this.accounts.putAll(builder.accounts);
        }
        if (builder.timestamp != null) {
            timestamp = ZonedDateTime.ofInstant(builder.timestamp.toInstant(), ZoneId.systemDefault());
        } else {
            timestamp = ZonedDateTime.now();
        }
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for accounts.
     *
     * @return accounts
     */
    public Map<String, AccountDTO> getAccounts() {
        return accounts;
    }

    /**
     * Getter for timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** User ID (usually username). */
        private String id;

        /** The accounts owned by this user. */
        private Map<String, AccountDTO> accounts = new LinkedHashMap<>();

        /** Information timestamp. */
        private Date timestamp;

        /**
         * Id.
         *
         * @param newId id.
         * @return builder
         */
        public Builder setId(final String newId) {
            this.id = newId;
            return this;
        }

        /**
         * Accounts.
         *
         * @param newAccounts accounts
         * @return builder
         */
        public Builder setAccounts(final Map<String, AccountDTO> newAccounts) {
            this.accounts = newAccounts;
            return this;
        }

        /**
         * Timestamp.
         *
         * @param newTimestamp timestamp
         * @return builder
         */
        public Builder timestamp(final Date newTimestamp) {
            this.timestamp = newTimestamp;
            return this;
        }

        /**
         * Creates user.
         *
         * @return user
         */
        public UserDTO create() {
            return new UserDTO(this);
        }

    }

}
