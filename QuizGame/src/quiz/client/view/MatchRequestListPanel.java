package quiz.client.view;

import quiz.client.IControl;
import quiz.client.model.ChangeType;
import quiz.client.model.IModel;
import quiz.client.model.Status;
import quiz.model.Account;
import quiz.model.Match;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import static quiz.Constants.FRAME_HEIGHT;

/**
 * @author Eric
 * @version 10.05.16
 */
public class MatchRequestListPanel extends JPanel implements IView {

    private IModel model;
    private IControl control;
    private List<Match> lastMatchRequests;
    private GameFrame gameFrame;
    private ResourceBundle localization = GameFrame.getLocalization();

    /**
     * Creates a new MatchRequestListPanel.
     *
     * @param gameFrame the current GameFrame instance
     * @param control   the IControl implementation
     * @param model     the IModel implementation
     */
    public MatchRequestListPanel(GameFrame gameFrame, IControl control, IModel model) {
        this.gameFrame = gameFrame;
        this.control = control;
        this.model = model;

        model.addView(this);
        setPreferredSize(new Dimension(175, FRAME_HEIGHT - 100));

        lastMatchRequests = new ArrayList<>();
    }

    @Override
    public void onChange(ChangeType type, Status status) {
        if (type == ChangeType.REQUESTS) {
            if (status == Status.ALREADY_REQUESTED) {
                gameFrame.showExceptionMessage(localization.getString("ALREADY_REQUESTED"));
                return;
            }

            List<Match> matchRequests = Arrays.asList(model.getRequests());

            // add all new match requests
            List<Match> newMatchRequests = new ArrayList<>(matchRequests);
            newMatchRequests.removeAll(lastMatchRequests);

            for (Match matchRequest : newMatchRequests) {
                MatchRequestPanel matchRequestPanel = new MatchRequestPanel(matchRequest);
                add(matchRequestPanel);

                revalidate();
                repaint();
            }

            // remove all old match requests
            lastMatchRequests.removeAll(matchRequests);

            for (Match matchRequest : lastMatchRequests) {
                for (ListIterator<Component> it = Arrays.asList(getComponents()).listIterator(); it.hasNext(); ) {
                    MatchRequestPanel next = (MatchRequestPanel) it.next();
                    if (next.matchRequest.getID() == matchRequest.getID()) {
                        remove(next);
                        revalidate();
                        repaint();
                        break;
                    }
                }
            }

            // update match requests
            lastMatchRequests = matchRequests;
        }
    }

    /**
     * @author Eric
     * @version 22.05.16
     */
    private class MatchRequestPanel extends JPanel {

        private final Match matchRequest;

        /**
         * Creates a new MatchRequestPanel.
         *
         * @param matchRequest the matchRequest
         */
        public MatchRequestPanel(Match matchRequest) {
            this.matchRequest = matchRequest;

            setMinimumSize(new Dimension(150, 75));
            setPreferredSize(new Dimension(200, 100));
            setMaximumSize(new Dimension(250, 125));
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBorder(BorderFactory.createLoweredBevelBorder());

            initComponents();
        }

        private void initComponents() {
            Account opponent = null;
            for (Account account : matchRequest.getOpponents()) {
                if (account.getID() != gameFrame.getUser().getID()) {
                    opponent = account;
                    break;
                }
            }

            JTextArea textArea = new JTextArea();
            MessageFormat formatter = new MessageFormat(localization.getString("RECEIVED_MATCHREQUEST"));

            String test = formatter.format(new Object[]{matchRequest.getCategory().toString(), opponent.getName()});
            textArea.setText(test);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(Color.LIGHT_GRAY);
            add(textArea);
            add(Box.createVerticalGlue());

            Box row = Box.createHorizontalBox();
            row.add(Box.createHorizontalGlue());

            JButton accept = new JButton(localization.getString("ACCEPT"));
            accept.addActionListener(e -> control.acceptRequest(matchRequest));
            row.add(accept);
            row.add(Box.createHorizontalGlue());

            JButton deny = new JButton(localization.getString("DENY"));
            deny.addActionListener(e -> control.denyRequest(matchRequest));
            row.add(deny);
            row.add(Box.createHorizontalGlue());

            add(row);
            add(Box.createVerticalGlue());
        }
    }
}