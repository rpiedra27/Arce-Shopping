package ucr.example.arce.controllers;

import ucr.example.arce.entities.CartItem;

import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
    Clase encargada de enviar correos electrónicos de contraseña temporal y confirmación de
    una compra.
 */
public class EmailSender {
    // Credenciales del correo electrónico desde donde se envían los correos
    private final String sEmail = "arceshoppingoficial@gmail.com";
    private final String sPass = "nxcamcemkwejhkiy";

    /*
     * Do: Crea el código HTML para el correo que se le envía a un usuario con la contraseña temporal.
     * Param: password: la contraseña temporal que el usuario debe cambiar.
     * Return: Contenido completo del correo en formato HTML.
     * */
    private String createPasswordEmailContent(String password) {
        return "<!doctype html>"+
                "<html>"+
                "  <head>"+
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
                "    <title>Arce Shopping</title>"+
                "    <style>"+
                "@media only screen and (max-width: 620px) {"+
                "  table.body h1 {"+
                "    font-size: 28px !important;"+
                "    margin-bottom: 10px !important;"+
                "  }"+
                ""+
                "  table.body p,"+
                "table.body ul,"+
                "table.body ol,"+
                "table.body td,"+
                "table.body span,"+
                "table.body a {"+
                "    font-size: 16px !important;"+
                "  }"+
                ""+
                "  table.body .wrapper,"+
                "table.body .article {"+
                "    padding: 10px !important;"+
                "  }"+
                ""+
                "  table.body .content {"+
                "    padding: 0 !important;"+
                "  }"+
                ""+
                "  table.body .container {"+
                "    padding: 0 !important;"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .main {"+
                "    border-left-width: 0 !important;"+
                "    border-radius: 0 !important;"+
                "    border-right-width: 0 !important;"+
                "  }"+
                ""+
                "  table.body .btn table {"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .btn a {"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .img-responsive {"+
                "    height: auto !important;"+
                "    max-width: 100% !important;"+
                "    width: auto !important;"+
                "  }"+
                ""+
                "  .btn-primary table td {"+
                "    padding-right: 0px;"+
                "  }"+
                "}"+
                "@media all {"+
                "  .ExternalClass {"+
                "    width: 100%;"+
                "  }"+
                ""+
                "  .ExternalClass,"+
                ".ExternalClass p,"+
                ".ExternalClass span,"+
                ".ExternalClass font,"+
                ".ExternalClass td,"+
                ".ExternalClass div {"+
                "    line-height: 100%;"+
                "  }"+
                ""+
                "  .apple-link a {"+
                "    color: inherit !important;"+
                "    font-family: inherit !important;"+
                "    font-size: inherit !important;"+
                "    font-weight: inherit !important;"+
                "    line-height: inherit !important;"+
                "    text-decoration: none !important;"+
                "  }"+
                ""+
                "  #MessageViewBody a {"+
                "    color: inherit;"+
                "    text-decoration: none;"+
                "    font-size: inherit;"+
                "    font-family: inherit;"+
                "    font-weight: inherit;"+
                "    line-height: inherit;"+
                "  }"+
                ""+
                "  .btn-primary table td:hover {"+
                "    background-color: #fff !important;"+
                "  }"+
                ""+
                "  .btn-primary a:hover {"+
                "    background-color: #34495e !important;"+
                "    border-color: #34495e !important;"+
                "  }"+
                "}"+
                "</style>"+
                "  </head>"+
                "  <body style=\"background-color: #f6f6f6; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">"+
                "    <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;\">Contraseña provisional Arce Shopping</span>"+
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #083AB0; width: 100%;\" width=\"100%\" bgcolor=\"#083AB0\">"+
                "      <tr>"+
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>"+
                "        <td class=\"container\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; margin: 0 auto;\" width=\"580\" valign=\"top\">"+
                "          <div class=\"content\" style=\"box-sizing: border-box; display: block; margin: 0 auto; max-width: 580px; padding: 10px;\">"+
                ""+
                "            <!-- START CENTERED WHITE CONTAINER -->"+
                "            <table role=\"presentation\" class=\"main\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #ffffff; border-radius: 3px; width: 100%;\" width=\"100%\">"+
                ""+
                "              <!-- START MAIN CONTENT AREA -->"+
                "              <tr>"+
                "                <td class=\"wrapper\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;\" valign=\"top\">"+
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">"+
                "                    <tr>"+
                "                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">"+
                "                        <h1 style=\"color: #000000; font-family: sans-serif; line-height: 1.4; margin: 0; margin-bottom: 30px; font-size: 30px; font-weight: 300; text-align: center; text-transform: capitalize;\">Bienvenido a nuestra aplicación</h1>"+
                "                        <p style=\"font-family: sans-serif; font-size: 20px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Su contraseña provisional para el primer inicio de sesión es: " + password + "</p>"+
                "                      </td>"+
                "                    </tr>"+
                "                  </table>"+
                "                </td>"+
                "              </tr>"+
                ""+
                "            <!-- END MAIN CONTENT AREA -->"+
                "            </table>"+
                "            <!-- END CENTERED WHITE CONTAINER -->"+
                ""+
                "            <!-- START FOOTER -->"+
                "            <div class=\"footer\" style=\"clear: both; margin-top: 10px; text-align: center; width: 100%;\">"+
                "              <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">"+
                "                <tr>"+
                "                  <td class=\"content-block\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #fff; font-size: 14px; text-align: center;\" valign=\"top\" align=\"center\">"+
                "                    <span class=\"apple-link\" style=\"color: #fff; font-size: 14px; text-align: center;\">Arce Shopping. San José, Costa Rica</span>"+
                "                  </td>"+
                "                </tr>"+
                "              </table>"+
                "            </div>"+
                "            <!-- END FOOTER -->"+
                "          </div>"+
                "        </td>"+
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>"+
                "      </tr>"+
                "    </table>"+
                "  </body>"+
                "</html>";
    }

    /*
     * Do: Crea el código HTML para el correo que se le envía a un usuario con los detalles de su compra.
     * Param: itemContent: contenido del carrito de compras convertido a HTML.
     * Param: totalPrice: precio total de la compra.
     * Return: Contenido completo del correo en formato HTML.
     * */
    private String createPurchaseConfirmationEmailContent(String itemContent, String totalPrice) {
        return "<!doctype html>"+
                "<html>"+
                "  <head>"+
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
                "    <title>Arce Shopping</title>"+
                "    <style>"+
                "@media only screen and (max-width: 620px) {"+
                "  table.body h1 {"+
                "    font-size: 28px !important;"+
                "    margin-bottom: 10px !important;"+
                "  }"+
                ""+
                "  table.body p,"+
                "table.body ul,"+
                "table.body ol,"+
                "table.body td,"+
                "table.body span,"+
                "table.body a {"+
                "    font-size: 16px !important;"+
                "  }"+
                ""+
                "  table.body .wrapper,"+
                "table.body .article {"+
                "    padding: 10px !important;"+
                "  }"+
                ""+
                "  table.body .content {"+
                "    padding: 0 !important;"+
                "  }"+
                ""+
                "  table.body .container {"+
                "    padding: 0 !important;"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .main {"+
                "    border-left-width: 0 !important;"+
                "    border-radius: 0 !important;"+
                "    border-right-width: 0 !important;"+
                "  }"+
                ""+
                "  table.body .btn table {"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .btn a {"+
                "    width: 100% !important;"+
                "  }"+
                ""+
                "  table.body .img-responsive {"+
                "    height: auto !important;"+
                "    max-width: 100% !important;"+
                "    width: auto !important;"+
                "  }"+
                ""+
                "  .btn-primary table td {"+
                "    padding-right: 0px;"+
                "  }"+
                "}"+
                "@media all {"+
                "  .ExternalClass {"+
                "    width: 100%;"+
                "  }"+
                ""+
                "  .ExternalClass,"+
                ".ExternalClass p,"+
                ".ExternalClass span,"+
                ".ExternalClass font,"+
                ".ExternalClass td,"+
                ".ExternalClass div {"+
                "    line-height: 100%;"+
                "  }"+
                ""+
                "  .apple-link a {"+
                "    color: inherit !important;"+
                "    font-family: inherit !important;"+
                "    font-size: inherit !important;"+
                "    font-weight: inherit !important;"+
                "    line-height: inherit !important;"+
                "    text-decoration: none !important;"+
                "  }"+
                ""+
                "  #MessageViewBody a {"+
                "    color: inherit;"+
                "    text-decoration: none;"+
                "    font-size: inherit;"+
                "    font-family: inherit;"+
                "    font-weight: inherit;"+
                "    line-height: inherit;"+
                "  }"+
                ""+
                "  .btn-primary table td:hover {"+
                "    background-color: #fff !important;"+
                "  }"+
                ""+
                "  .btn-primary a:hover {"+
                "    background-color: #34495e !important;"+
                "    border-color: #34495e !important;"+
                "  }"+
                "}"+
                "</style>"+
                "  </head>"+
                "  <body style=\"background-color: #f6f6f6; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">"+
                "    <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;\">ConfirmaciÃ³n de tu compra con Arce Shopping</span>"+
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #083AB0; width: 100%;\" width=\"100%\" bgcolor=\"#083AB0\">"+
                "      <tr>"+
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>"+
                "        <td class=\"container\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; margin: 0 auto;\" width=\"580\" valign=\"top\">"+
                "          <div class=\"content\" style=\"box-sizing: border-box; display: block; margin: 0 auto; max-width: 580px; padding: 10px;\">"+
                ""+
                "            <!-- START CENTERED WHITE CONTAINER -->"+
                "            <table role=\"presentation\" class=\"main\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #ffffff; border-radius: 3px; width: 100%;\" width=\"100%\">"+
                ""+
                "              <!-- START MAIN CONTENT AREA -->"+
                "              <tr>"+
                "                <td class=\"wrapper\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;\" valign=\"top\">"+
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">"+
                "                    <tr>"+
                "                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">"+
                "                        <h1 style=\"color: #000000; font-family: sans-serif; line-height: 1.4; margin: 0; margin-bottom: 30px; font-size: 30px; font-weight: 300; text-align: center; text-transform: capitalize;\">Confirmación de tu compra con Arce Shopping</h1>"+
                "                        <p style=\"font-family: sans-serif; font-size: 20px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Hola, recibimos tu pedido y tu orden está siendo procesada.</p>"+
                "                        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; box-sizing: border-box; width: 100%;\" width=\"100%\">"+
                "                          <tbody>"+
                "                            <tr>"+
                "                              <td align=\"left\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; padding-bottom: 15px;\" valign=\"top\">"+
                "                                <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: auto;\">"+
                "                                  <tbody>"+
                itemContent +
                "                                  </tbody>"+
                "                                </table>"+
                "                              </td>"+
                "                            </tr>"+
                "                          </tbody>"+
                "                        </table>"+
                "                        <p style=\"font-family: sans-serif; font-size: 20px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Total: $" + totalPrice + "</p>"+
                "                        <p style=\"font-family: sans-serif; font-size: 20px; font-weight: normal; margin: 0; margin-bottom: 15px;\">¡Gracias por tu compra!</p>"+
                "                      </td>"+
                "                    </tr>"+
                "                  </table>"+
                "                </td>"+
                "              </tr>"+
                ""+
                "            <!-- END MAIN CONTENT AREA -->"+
                "            </table>"+
                "            <!-- END CENTERED WHITE CONTAINER -->"+
                ""+
                "            <!-- START FOOTER -->"+
                "            <div class=\"footer\" style=\"clear: both; margin-top: 10px; text-align: center; width: 100%;\">"+
                "              <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">"+
                "                <tr>"+
                "                  <td class=\"content-block\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #fff; font-size: 14px; text-align: center;\" valign=\"top\" align=\"center\">"+
                "                    <span class=\"apple-link\" style=\"color: #fff; font-size: 14px; text-align: center;\">Arce Shopping. San José, Costa Rica</span>"+
                "                  </td>"+
                "                </tr>"+
                "              </table>"+
                "            </div>"+
                "            <!-- END FOOTER -->"+
                ""+
                "          </div>"+
                "        </td>"+
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>"+
                "      </tr>"+
                "    </table>"+
                "  </body>"+
                "</html>";
    }

    /*
     * Do: Obtiene el contenido del correo en formato HTML de otros métodos, define un asunto y envía
     * los datos al método que finalmente envía el correo de contraseña temporal.
     * Param: recipient: el correo electrónico del usuario recipiente.
     * Param: password: la contraseña temporal que el usuario debe cambiar.
     * Return: Nada
     * */
    public void sendPasswordEmail(String recipient, String password) {
        String content = createPasswordEmailContent(password);
        String subject = "Contraseña provisional Arce Shopping";
        sendEmail(recipient, content, subject);
    }

    /*
     * Do: Obtiene el contenido del correo en formato HTML de otros métodos, define un asunto y envía
     * los datos al método que finalmente envía el correo de confirmación de compra.
     * Param: recipient: el correo electrónico del usuario recipiente.
     * Param: items: la lista de artículos que el usuario compró.
     * Return: Nada
     * */
    public void sendPurchaseConfirmation(String recipient, List<CartItem> items) {
        String itemContent = convertCartToHTML(items);
        String totalPrice = getCartTotal(items);
        String content = createPurchaseConfirmationEmailContent(itemContent, totalPrice);
        String subject = "Confirmación de compra en Arce Shopping";
        sendEmail(recipient, content, subject);
    }

    /*
     * Do: Envía el correo electrónico al usuario.
     * Param: recipient: el correo electrónico del usuario recipiente.
     * Param: content: contenido del correo electrónico en formato HTML.
     * Param: subject: asunto con el que se enviará el correo.
     * Return: Nada
     * */
    private void sendEmail(String recipient, String content, String subject) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail, sPass);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MimeMessage mm = new MimeMessage(session);
                    mm.setFrom(new InternetAddress(sEmail));
                    mm.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                    mm.setSubject(subject);
                    mm.setContent(content, "text/html; charset=utf-8");
                    Transport.send(mm);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
     * Do: Convierte una lista de objetos tipo CartItem a formato HTML.
     * Param: items: lista de objetos tipo CartItem provenientes del carrito de compras.
     * Return: Contenido de la lista en formato HTML.
     * */
    private String convertCartToHTML(List<CartItem> items) {
        String htmlString = "";
            for(int i = 0; i < items.size(); i++) {
                if(items.get(i).getQuantity() > 0) {
                    htmlString += "<tr>"+
                            " <td style=\"font-family: sans-serif; vertical-align: top; border-radius: 5px; font-size: 18px; background-color: #fff; padding-right: 60px; padding-bottom: 10px; text-align: center;\" valign=\"top\" bgcolor=\"#fff\" align=\"center\"><img src= \"" + items.get(i).getThumbnail() + "\" alt=\"Imagen del producto\" width=\"120\" height=\"100\" border=\"0\" style=\"-ms-interpolation-mode: bicubic; max-width: 100%; border: 0; outline: none; text-decoration: none; display: block;\"></td>"+
                            " <td style=\"font-family: sans-serif; vertical-align: top; border-radius: 5px; font-size: 18px; background-color: #fff; padding-right: 60px; padding-bottom: 10px; text-align: center;\" valign=\"top\" bgcolor=\"#fff\" align=\"center\">" + items.get(i).getQuantity() + " x " + items.get(i).getName() + "</td>" +
                            " <td style=\"font-family: sans-serif; vertical-align: top; border-radius: 5px; font-size: 18px; background-color: #fff; padding-right: 60px; padding-bottom: 10px; text-align: center;\" valign=\"top\" bgcolor=\"#fff\" align=\"center\"> $" + items.get(i).getPrice() * items.get(i).getQuantity() + "</td>"+
                            " </tr>";
                }
            }
        return htmlString;
    }

    /*
     * Do: Obtiene el precio total de la compra del usuario.
     * Param: items: lista de objetos tipo CartItem provenientes del carrito de compras.
     * Return: Precio total de la compra, se convierte a String para agregarlo al String HTML posteriormente.
     * */
    private String getCartTotal(List<CartItem> items) {
        int total = 0;
        for(int i = 0; i < items.size(); i++) {
            total += items.get(i).getPrice() * items.get(i).getQuantity();
        }
        return String.valueOf(total);
    }
}