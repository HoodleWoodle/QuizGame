package quiz.client.view;

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

    private Map<Account, PlayerPanel> accounts = new HashMap<Account, PlayerPanel>();
    private IModel model;
    private ResourceBundle localization = GameFrame.getLocalization();
    private GameFrame gameFrame;

    /**
     * Creates a new PlayerListPanel.
     *
     * @param gameFrame the GameFrame
     * @param model   the IModel implementation
     */
    public PlayerListPanel(GameFrame gameFrame, IModel model) {
        this.gameFrame = gameFrame;
        this.model = model;

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
                if (account.isOnline()) {
                    if (!accounts.containsKey(account)) {
                        PlayerPanel playerPanel = new PlayerPanel(account);
                        accounts.put(account, playerPanel);
                        add(playerPanel);

                        revalidate();
                        repaint();
                    } else
                        accounts.get(account).updateStatus();

                } else if (accounts.containsKey(account)) {
                    remove(accounts.get(account));
                    revalidate();
                    repaint();
                    accounts.remove(account);
                }
            }
        }
    }

    /**
     * @author Eric
     * @version 22.05.16
     */
    private class PlayerPanel extends JPanel {

        private final Account account;
        private final JLabel status;

        /**
         * Creates a new PlayerPanel with the given Account.
         *
         * @param account the Account
         */
        public PlayerPanel(Account account) {
            this.account = account;

            setMinimumSize(new Dimension(100, 50));
            setPreferredSize(new Dimension(150, 50));
            setMaximumSize(new Dimension(200, 50));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLoweredBevelBorder());

            add(status = new JLabel(), BorderLayout.CENTER);
            updateStatus();

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem matchRequest = new JMenuItem(localization.getString("CHALLENGE"));
            matchRequest.addActionListener(event -> {
                gameFrame.getChallengeDialog().setSearchOpponent(true);
                gameFrame.getChallengeDialog().reset();
            });

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
         * Reflects the current status of the account in view.
         */
        public void updateStatus() {
            status.setText(account.getName() +
                    (!account.isAvailable() ? " (" + localization.getString("STATUS_IN_GAME") + ")" :
                            " (" + localization.getString("STATUS_ONLINE") + ")"));
        }
    }
}