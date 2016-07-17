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
    private List<Match> lastMatchRequests, lastSentMatchRequests;
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
        lastSentMatchRequests = new ArrayList<>();
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
                MatchRequestPanel matchRequestPanel = new MatchRequestPanel(matchRequest, MatchRequestPanel.RECEIVED);
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

        if(type == ChangeType.SENT_REQUESTS) {
            List<Match> matchRequests = Arrays.asList(model.getSentRequests());

            // add all new sent match requests
            List<Match> newMatchRequests = new ArrayList<>(matchRequests);
            newMatchRequests.removeAll(lastSentMatchRequests);

            for (Match matchRequest : newMatchRequests) {
                MatchRequestPanel matchRequestPanel = new MatchRequestPanel(matchRequest, MatchRequestPanel.SENT);
                add(matchRequestPanel);

                revalidate();
                repaint();
            }

            // remove all old sent match requests
            lastSentMatchRequests.removeAll(matchRequests);

            for (Match matchRequest : lastSentMatchRequests) {
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

            // update sent match requests
            lastSentMatchRequests = matchRequests;
        }
    }

    /**
     * @author Eric
     * @version 22.05.16
     */
    private class MatchRequestPanel extends JPanel {

        public static final int SENT = 1;
        public static final int RECEIVED = 2;
        private final Match matchRequest;
        private int type;

        /**
         * Creates a new MatchRequestPanel.
         *
         * @param matchRequest the matchRequest
         * @param type the match request type
         *
         * @throws IllegalArgumentException when type is neither SENT nor RECEIVED
         */
        public MatchRequestPanel(Match matchRequest, int type) {
            if(type < 1 || type > 2)
                throw new IllegalArgumentException("Type must either be SENT or RECEIVED!");
            this.type = type;
            this.matchRequest = matchRequest;

            setMinimumSize(new Dimension(150, (type == SENT) ?  50 : 75));
            setPreferredSize(new Dimension(200, (type == SENT) ? 75 : 100));
            setMaximumSize(new Dimension(250, (type == SENT) ? 100 : 125));
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

            String text = null;
            if(type == SENT)
                text = localization.getString("SENT_MATCHREQUEST");
            else if(type == RECEIVED)
                text = localization.getString("RECEIVED_MATCHREQUEST");
            JTextArea textArea = new JTextArea();
            MessageFormat formatter = new MessageFormat(text);

            String test = formatter.format(new Object[]{opponent.getName(), matchRequest.getCategory().toString()});
            textArea.setText(test);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(Color.LIGHT_GRAY);
            add(textArea);
            add(Box.createVerticalGlue());

            Box row = Box.createHorizontalBox();
            row.add(Box.createHorizontalGlue());

            if(type == RECEIVED) {
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
}