package ConsumidorCola;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ConsumidorCola {

    public static void main(String [] args) throws Exception {
        thread(new Consumidor(), false);
        Thread.sleep(1000);
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class Consumidor implements Runnable, ExceptionListener {
        public void run() {
            try {
            	//ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://172.16.9.166:61617");
            	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://172.16.2.190:61617");

            	connectionFactory.setUserName("admin");
                connectionFactory.setPassword("2019_Esb_PRD_JB0ss");
                
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Destination destination = session.createQueue("COLA.PRUEBA");

                MessageConsumer consumer = session.createConsumer(destination);

                Message message = consumer.receive(1000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Mensaje Recibido: " + text);
                } else {
                    System.out.println("Mensaje Recibido: " + message);
                }

                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Excepcion: " + e);
                e.printStackTrace();
            }
        }

        public synchronized void onException(JMSException ex) {
            System.out.println("Ocurrio una excepcion...");
        }
    }
}
