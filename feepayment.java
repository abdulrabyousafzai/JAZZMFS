package com.mfs.passportfeepayment;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;



public class feepayment
  extends AbstractMediator
{
  Log logger = LogFactory.getLog(feepayment.class);
  
  public feepayment() {}
  
  public boolean mediate(MessageContext msgCtx) {
    logger.debug("[FeePayment] Init");
    String CNIC = (String)msgCtx.getProperty("CNIC");
    String CUSTOMER_MSISDN = (String)msgCtx.getProperty("Customer_MSISDN");
    String PASSPORT_TYPE = (String)msgCtx.getProperty("Passport_Type");
    String PASSPORT_CATEGORY = (String)msgCtx.getProperty("Passport_Category");
    String PAYMENT_SOURCE = (String)msgCtx.getProperty("PAYMENT_SOURCE");
    
    String PASSPORT_PAGES1 = (String)msgCtx.getProperty("Passport_Pages");
    Double PASSPORT_PAGES = Double.valueOf(Double.parseDouble(PASSPORT_PAGES1));
    
    String HOME_DELIVERY = (String)msgCtx.getProperty("Home_Delivery");
    String PAYMENT_TYPE = (String)msgCtx.getProperty("Payment_Type");
    

    String VALIDITY1 = (String)msgCtx.getProperty("Validity");
    Double VALIDITY = Double.valueOf(Double.parseDouble(VALIDITY1));
    
    String PASSPORT_FEE1 = (String)msgCtx.getProperty("Passport_Fee");
    Double PASSPORT_FEE = Double.valueOf(Double.parseDouble(PASSPORT_FEE1));
    
    String TRANSACTION_CHARGES1 = (String)msgCtx.getProperty("Transaction_Charges");
    Double TRANSACTION_CHARGES = Double.valueOf(Double.parseDouble(TRANSACTION_CHARGES1));
    
    String HOME_DELIVERY_AMOUNT1 = (String)msgCtx.getProperty("Home_Delivery_Amount");
    Double HOME_DELIVERY_AMOUNT = Double.valueOf(Double.parseDouble(HOME_DELIVERY_AMOUNT1));
    
    String FED1 = (String)msgCtx.getProperty("FED");
    Double FED = Double.valueOf(Double.parseDouble(FED1));
    
    String TOTAL_AMOUNT1 = (String)msgCtx.getProperty("Total_Amount");
    Double TOTAL_AMOUNT = Double.valueOf(Double.parseDouble(TOTAL_AMOUNT1));
    
    String TRANSACTIONID = (String)msgCtx.getProperty("TRANSACTIONID");
    String RESERVEDFIELD1 = (String)msgCtx.getProperty("ReservedField1");
    String RESERVEDFIELD2 = (String)msgCtx.getProperty("ReservedField2");
    String RESERVEDFIELD3 = (String)msgCtx.getProperty("ReservedField3");
    

    CallableStatement callableStatement = null;
    Connection conn = null;
    Hashtable<String, String> environment = new Hashtable();
    environment.put("java.naming.factory.initial", "org.wso2.carbon.tomcat.jndi.CarbonJavaURLContextFactory");
    
    try
    {
      Context initContext = new InitialContext(environment);
      DataSource ds = (DataSource)initContext.lookup("jdbc/mfsstg");
      if (ds != null) {
        conn = ds.getConnection();
      }
      
      String simpleProc = "{ call MPFC.SAVE_PASSPORT_DATA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
      
      callableStatement = conn.prepareCall(simpleProc);
      callableStatement.setString(1, CNIC);
      callableStatement.setString(2, CUSTOMER_MSISDN);
      callableStatement.setString(3, PASSPORT_TYPE);
      callableStatement.setString(4, PASSPORT_CATEGORY);
      callableStatement.setString(5, PAYMENT_SOURCE);
      callableStatement.setDouble(6, PASSPORT_PAGES.doubleValue());
      callableStatement.setString(7, HOME_DELIVERY);
      callableStatement.setString(8, PAYMENT_TYPE);
      callableStatement.setDouble(9, VALIDITY.doubleValue());
      callableStatement.setDouble(10, PASSPORT_FEE.doubleValue());
      callableStatement.setDouble(11, TRANSACTION_CHARGES.doubleValue());
      callableStatement.setDouble(12, HOME_DELIVERY_AMOUNT.doubleValue());
      callableStatement.setDouble(13, FED.doubleValue());
      callableStatement.setDouble(14, TOTAL_AMOUNT.doubleValue());
      callableStatement.setString(15, TRANSACTIONID);
      callableStatement.setString(16, RESERVEDFIELD1);
      callableStatement.setString(17, RESERVEDFIELD2);
      callableStatement.setString(18, RESERVEDFIELD3);
      callableStatement.registerOutParameter(19, 12);
      callableStatement.registerOutParameter(20, 12);
      callableStatement.registerOutParameter(21, 12);
      
      callableStatement.executeUpdate();
      String STATUS = callableStatement.getString(19);
      String Reason = callableStatement.getString(20);
      String UNIQUE_IDENTIFIER = callableStatement.getString(21);
      log.debug("[Check Warid Msisdn] SP Execution finished  we got " + STATUS);
      msgCtx.setProperty("STATUS", STATUS);
      msgCtx.setProperty("REASON", Reason);
      msgCtx.setProperty("UNIQUE_IDENTIFIER", UNIQUE_IDENTIFIER);
    }
    catch (Exception e) {
      String STATUS = "KO";
      String Reason = "Database Down";
      msgCtx.setProperty("STATUS", STATUS);
      msgCtx.setProperty("REASON", Reason);
      e.printStackTrace();
      
      if (callableStatement != null) {
        try {
          callableStatement.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
        try {
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (callableStatement != null) {
        try {
          callableStatement.close();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
        try {
          if (conn != null) {
            conn.close();
          }
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    

    return true;
  }
}
