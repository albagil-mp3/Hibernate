package com.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.billing.model.party.Client;
import com.billing.util.JdbcUtil;

public class ClientJdbcDAO  {

    private static final Logger logger = Logger.getLogger(ClientJdbcDAO.class.getName());

    public Client save(Client client) {
        String partySql = "INSERT INTO party (name,address,email,city,province,postal_code,fixed_phone,mobile_phone,website) VALUES (?,?,?,?,?,?,?,?,?)";
        String clientSql = "INSERT INTO client (id,dni,code,payment_method,credit_limit,bank_account_number,active,observations) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = JdbcUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement psParty = c.prepareStatement(partySql, Statement.RETURN_GENERATED_KEYS)) {
                    psParty.setString(1, client.getName());
                    psParty.setString(2, client.getAddress());
                    psParty.setString(3, client.getEmail());
                    psParty.setString(4, client.getCity());
                    psParty.setString(5, client.getProvince() == null ? null : client.getProvince().name());
                    psParty.setString(6, client.getPostalCode());
                    psParty.setString(7, client.getFixedPhone());
                    psParty.setString(8, client.getMobilePhone());
                    psParty.setString(9, client.getWebsite());

                    int affected = psParty.executeUpdate();
                    if (affected == 0) throw new SQLException("Creating party failed, no rows affected.");
                    try (ResultSet keys = psParty.getGeneratedKeys()) {
                        if (keys.next()) {
                            client.setId(keys.getLong(1));
                        }
                    }
                }
                try (PreparedStatement psClient = c.prepareStatement(clientSql)) {
                    psClient.setLong(1, client.getId());
                    psClient.setString(2, client.getDni());
                    psClient.setString(3, client.getCode());
                    psClient.setString(4, client.getPaymentMethod() == null ? null : client.getPaymentMethod().name());
                    psClient.setObject(5, client.getCreditLimit());
                    psClient.setString(6, client.getBankAccountNumber());
                    psClient.setBoolean(7, client.getActive() == null ? true : client.getActive());
                    psClient.setString(8, client.getObservations());
                    psClient.executeUpdate();
                }
                c.commit();
                return client;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving client via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    public Client findById(Long id) {
        String sql = "SELECT p.*, c.dni, c.code, c.payment_method, c.credit_limit, c.bank_account_number, c.active, c.observations " +
                     "FROM client c JOIN party p ON p.id = c.id WHERE c.id = ?";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding client by id via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    public List<Client> findAll() {
        String sql = "SELECT p.*, c.dni, c.code, c.payment_method, c.credit_limit, c.bank_account_number, c.active, c.observations " +
                     "FROM client c JOIN party p ON p.id = c.id";
        List<Client> list = new ArrayList<>();
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all clients via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    public Client update(Client client) {
        String partySql = "UPDATE party SET name=?,address=?,email=?,city=?,province=?,postal_code=?,fixed_phone=?,mobile_phone=?,website=? WHERE id = ?";
        String clientSql = "UPDATE client SET dni=?,code=?,payment_method=?,credit_limit=?,bank_account_number=?,active=?,observations=? WHERE id = ?";
        try (Connection c = JdbcUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement psParty = c.prepareStatement(partySql)) {
                    psParty.setString(1, client.getName());
                    psParty.setString(2, client.getAddress());
                    psParty.setString(3, client.getEmail());
                    psParty.setString(4, client.getCity());
                    psParty.setString(5, client.getProvince() == null ? null : client.getProvince().name());
                    psParty.setString(6, client.getPostalCode());
                    psParty.setString(7, client.getFixedPhone());
                    psParty.setString(8, client.getMobilePhone());
                    psParty.setString(9, client.getWebsite());
                    psParty.setLong(10, client.getId());
                    psParty.executeUpdate();
                }
                try (PreparedStatement psClient = c.prepareStatement(clientSql)) {
                    psClient.setString(1, client.getDni());
                    psClient.setString(2, client.getCode());
                    psClient.setString(3, client.getPaymentMethod() == null ? null : client.getPaymentMethod().name());
                    psClient.setObject(4, client.getCreditLimit());
                    psClient.setString(5, client.getBankAccountNumber());
                    psClient.setBoolean(6, client.getActive() == null ? true : client.getActive());
                    psClient.setString(7, client.getObservations());
                    psClient.setLong(8, client.getId());
                    psClient.executeUpdate();
                }
                c.commit();
                return client;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating client via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    public void delete(Client client) {
        if (client != null) deleteById(client.getId());
    }

    public void deleteById(Long id) {
        String clientSql = "DELETE FROM client WHERE id = ?";
        String partySql = "DELETE FROM party WHERE id = ?";
        try (Connection c = JdbcUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(clientSql)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(partySql)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting client via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    public Client findByDni(String dni) {
        String sql = "SELECT p.*, c.dni, c.code, c.payment_method, c.credit_limit, c.bank_account_number, c.active, c.observations " +
                     "FROM client c JOIN party p ON p.id = c.id WHERE c.dni = ?";
        try (Connection c = JdbcUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding client by dni via JDBC", e);
            throw new RuntimeException(e);
        }
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setAddress(rs.getString("address"));
        // code
        c.setCode(rs.getString("code"));
        // dni
        c.setDni(rs.getString("dni"));
        // location / contact (from Party)
        c.setCity(rs.getString("city"));
        String provinceStr = rs.getString("province");
        if (provinceStr != null) {
            c.setProvince(com.billing.model.SpanishProvince.valueOf(provinceStr));
        }
        c.setPostalCode(rs.getString("postal_code"));
        c.setFixedPhone(rs.getString("fixed_phone"));
        c.setMobilePhone(rs.getString("mobile_phone"));
        c.setEmail(rs.getString("email"));
        c.setWebsite(rs.getString("website"));
        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) {
            c.setPaymentMethod(com.billing.model.PaymentMethod.valueOf(paymentMethodStr));
        }
        c.setCreditLimit(rs.getBigDecimal("credit_limit"));
        c.setBankAccountNumber(rs.getString("bank_account_number"));
        c.setActive(rs.getBoolean("active"));
        c.setObservations(rs.getString("observations"));
        return c;
    }
}
