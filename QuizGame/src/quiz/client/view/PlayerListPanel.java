package quiz.client.view;

import quiz.ImageResourceLoader;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
     * @param model     the IModel implementation
     */
    public PlayerListPanel(GameFrame gameFrame, IModel model) {
        this.gameFrame = gameFrame;
        this.model = model;
        
        Icon[] icons = ImageResourceLoader.loadIcons();
		online = icons[0];
		not_available = icons[1];
		offline = icons[2];

        model.addView(this);
        setMinimumSize(new Dimension(100, FRAME_HEIGHT - 200));
        setPreferredSize(new Dimension(150, FRAME_HEIGHT - 100));
        setMaximumSize(new Dimension(200, FRAME_HEIGHT));
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        // update account statuses
        if (type == ChangeType.OPPONENTS) {
            int combinedHeight = 0, count = 0;
            for (Account account : model.getOpponents()) {
                PlayerPanel playerPanel;
                if (accounts.containsKey(account)) {
                    playerPanel = accounts.get(account);
                    playerPanel.setAccount(account);
                    playerPanel.updateStatus();
                } else {
                    playerPanel = new PlayerPanel(account);
                    accounts.put(account, playerPanel);
                    add(playerPanel);

                    revalidate();
                    repaint();
                }

                combinedHeight += playerPanel.getHeight() + 35;
            }

            setSize(new Dimension(getWidth(), combinedHeight));
            setPreferredSize(new Dimension(getWidth(), combinedHeight));
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

            add(status = new JLabel(account.getName() + " (" + localization.getString("SCORE") + ": "
                    + account.getScore() + ")"), BorderLayout.CENTER);
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
            status.setText(account.getName() + " (" + localization.getString("SCORE") + ": " + account.getScore() + ")");
            if (!account.isOnline()) {
                status.setIcon(offline);
                status.setToolTipText(localization.getString("STATUS_OFFLINE"));
            } else if (account.isAvailable()) {
                status.setIcon(online);
                status.setToolTipText(localization.getString("STATUS_ONLINE"));
            } else {
                status.setIcon(not_available);
                status.setToolTipText(localization.getString("STATUS_IN_GAME"));
            }
        }
    }
}