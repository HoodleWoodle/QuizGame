package quiz.client.view;

import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static quiz.Constants.FRAME_HEIGHT;

/**
 * @author Eric
 * @version 8.05.16
 */
public class PlayerListPanel extends JPanel implements IView {

    private Map<Account, PlayerPanel> accounts = new HashMap<>();
    private IModel model;
    private ResourceBundle localization = GameFrame.getLocalization();
    private GameFrame gameFrame;
    private Icon online, offline, not_available;

    /**
     * Creates a new PlayerListPanel.
     *
     * @param gameFrame the GameFrame
     * @param model   the IModel implementation
     */
    public PlayerListPanel(GameFrame gameFrame, IModel model) {
        this.gameFrame = gameFrame;
        this.model = model;

        try {
            online = new ImageIcon(ImageIO.read(Paths.get("data").resolve("icon_online.png").toFile()));
            not_available = new ImageIcon(ImageIO.read(Paths.get("data").resolve("icon_not_available.png").toFile()));
            offline = new ImageIcon(ImageIO.read(Paths.get("data").resolve("icon_offline.png").toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addView(this);
        setMinimumSize(new Dimension(100, FRAME_HEIGHT - 200));
        setPreferredSize(new Dimension(150, FRAME_HEIGHT - 100));
        setMaximumSize(new Dimension(200, FRAME_HEIGHT));
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        // update account statuses
        if (type == ChangeType.OPPONENTS) {
            for (Account account : model.getOpponents()) {
                if (accounts.containsKey(account)) {
                    PlayerPanel playerPanel = accounts.get(account);
                    playerPanel.setAccount(account);
                    playerPanel.updateStatus();
                } else {
                    PlayerPanel playerPanel = new PlayerPanel(account);
                    accounts.put(account, playerPanel);
                    add(playerPanel);

                    revalidate();
                    repaint();
                }
            }
        }
    }

    /**
     * @author Eric
     * @version 22.05.16
     */
    private class PlayerPanel extends JPanel {

        private Account account;
        private final JLabel status;

        /**
         * Creates a new PlayerPanel with the given Account.
         *
         * @param account the Account
         */
        public PlayerPanel(Account account) {
            this.account = account;

            setPreferredSize(new Dimension(175, 30));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createRaisedBevelBorder());
            setBackground(Color.LIGHT_GRAY);

            add(status = new JLabel(account.getName() + " (Score: " + account.getScore() + ")"), BorderLayout.CENTER);
            status.setIconTextGap(20);
            updateStatus();

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem matchRequest = new JMenuItem(localization.getString("CHALLENGE"));
            matchRequest.addActionListener(event -> gameFrame.getMenuPanel().getOpponentName().setText(account.getName()));

            popupMenu.add(matchRequest);
            status.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent event) {
                    if (event.isPopupTrigger())
                        popupMenu.show(PlayerPanel.this, event.getX(), event.getY());
                }
            });
        }

        /**
         * Sets the account used for this player panel.
         *
         * @param account the account used for this player panel
         */
        public void setAccount(Account account) {
            this.account = account;
        }

        /**
         * Returns the account used for this player panel.
         *
         * @return the account used for this player panel
         */
        public Account getAccount() {
            return account;
        }

        /**
         * Updates the current status of the account in view.
         */
        public void updateStatus() {
            status.setText(account.getName() + " (Score: " +  account.getScore() + ")");
            if(!account.isOnline()) {
                status.setIcon(offline);
                status.setToolTipText(localization.getString("STATUS_OFFLINE"));
            }
            else if(account.isAvailable()) {
                status.setIcon(online);
                status.setToolTipText(localization.getString("STATUS_ONLINE"));
            }
            else {
                status.setIcon(not_available);
                status.setToolTipText(localization.getString("STATUS_IN_GAME"));
            }
        }
    }
}