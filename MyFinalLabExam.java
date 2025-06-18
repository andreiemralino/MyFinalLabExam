import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class CalculatorUI extends JFrame implements ActionListener {
    private final int BUTTON_SIZE = 60;
    private JTextField expressionField;
    private JTextField resultField;
    private StringBuilder currentInput = new StringBuilder();
    private boolean hasDecimal = false;
    private boolean hasExponent = false;
    private boolean hasOperator = false;

    public CalculatorUI() {
        setTitle("Calculator");
        int cols = 4;
        int rows = 6;
        int gap = 8;

        int panelWidth = (BUTTON_SIZE + gap) * cols + gap;
        int panelHeight = (BUTTON_SIZE + gap) * rows + gap;
        int displayHeight = 100;

        setSize(panelWidth + 30, panelHeight + displayHeight + 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(188, 214, 180));

        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        displayPanel.setPreferredSize(new Dimension(panelWidth, displayHeight));

        expressionField = new JTextField();
        expressionField.setEditable(false);
        expressionField.setBackground(new Color(172, 199, 157));
        expressionField.setFont(new Font("Arial", Font.PLAIN, 22));
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionField.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        resultField = new JTextField();
        resultField.setEditable(false);
        resultField.setBackground(new Color(172, 199, 157));
        resultField.setFont(new Font("Arial", Font.BOLD, 26));
        resultField.setHorizontalAlignment(SwingConstants.RIGHT);
        resultField.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        displayPanel.add(expressionField);
        displayPanel.add(resultField);
        mainPanel.add(displayPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
        buttonPanel.setBackground(new Color(188, 214, 180));

        String[][] layout = {
            {"%", "^", "C", "←"},
            {"√", "/", "*", "-"},
            {"7", "8", "9", "+"},
            {"4", "5", "6", ""},
            {"1", "2", "3", "="},
            {"0", "0", ".", ""}
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(gap / 2, gap / 2, gap / 2, gap / 2);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                String text = layout[row][col];
                if (text.equals("")) continue;

                RoundedButton button = new RoundedButton(text);
                button.setFont(new Font("Arial", Font.BOLD, 18));
                button.setBackground(new Color(142, 180, 123));
                button.setForeground(new Color(56, 20, 71));
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.addActionListener(this);

                gbc.gridx = col;
                gbc.gridy = row;

                if (text.equals("0") && row == 5 && col == 0) {
                    gbc.gridwidth = 2;
                } else if (text.equals("+") && row == 2) {
                    gbc.gridheight = 2;
                } else if (text.equals("=") && row == 4) {
                    gbc.gridheight = 2;
                } else {
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;
                }

                buttonPanel.add(button, gbc);
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
            }
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        char last = currentInput.length() > 0 ? currentInput.charAt(currentInput.length() - 1) : ' ';

        switch (command) {
            case "C":
                currentInput.setLength(0);
                expressionField.setText("");
                resultField.setText("");
                hasDecimal = false;
                hasExponent = false;
                hasOperator = false;
                break;

            case "←":
                if (currentInput.length() > 0) {
                    char lastChar = currentInput.charAt(currentInput.length() - 1);
                    if (lastChar == '.') hasDecimal = false;
                    if (lastChar == '^') hasExponent = false;
                    if ("+-*/^%".indexOf(lastChar) >= 0) hasOperator = false;
                    currentInput.setLength(currentInput.length() - 1);
                    expressionField.setText(currentInput.toString());
                }
                break;

            case "=":
                if (currentInput.length() == 0) return;
                try {
                    BigDecimal result = evaluateExpression(currentInput.toString());
                    resultField.setText(result.stripTrailingZeros().toPlainString());
                    expressionField.setText(currentInput.toString());
                    currentInput.setLength(0);
                    currentInput.append(result.stripTrailingZeros().toPlainString());
                } catch (Exception ex) {
                    resultField.setText("Error");
                    currentInput.setLength(0);
                }
                hasDecimal = false;
                hasExponent = false;
                hasOperator = false;
                break;

            case "%":
                if (currentInput.length() > 0 && Character.isDigit(last)) {
                    currentInput.append('%');
                    expressionField.setText(currentInput.toString());
                    hasOperator = true;
                }
                break;

            case "^":
                if (!hasExponent && currentInput.length() > 0 && Character.isDigit(last)) {
                    currentInput.append('^');
                    hasExponent = true;
                    hasDecimal = false;
                    hasOperator = true;
                    expressionField.setText(currentInput.toString());
                }
                break;

            case ".":
                if (!hasDecimal) {
                    currentInput.append('.');
                    hasDecimal = true;
                    expressionField.setText(currentInput.toString());
                }
                break;

            case "√":
                if (currentInput.length() > 0) {
                    char lastChar = currentInput.charAt(currentInput.length() - 1);
                    if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '%') {
                        currentInput.append("*");
                    }
                }
                currentInput.append("√");
                expressionField.setText(currentInput.toString());
                break;

            default:
                if ("0123456789".contains(command)) {
                    if (last == '%') currentInput.append('*');
                    currentInput.append(command);
                } else if ("+-*/".contains(command)) {
                    if ("+-*/".contains(String.valueOf(last))) return;

                    if (hasOperator) {
                        try {
                            BigDecimal result = evaluateExpression(currentInput.toString());
                            resultField.setText(result.stripTrailingZeros().toPlainString());
                            currentInput.setLength(0);
                            currentInput.append(result.stripTrailingZeros().toPlainString());
                        } catch (Exception ex) {
                            resultField.setText("Error");
                            currentInput.setLength(0);
                            hasOperator = false;
                            break;
                        }
                    }

                    currentInput.append(command);
                    hasOperator = true;
                    hasDecimal = false;
                    hasExponent = false;
                } else {
                    currentInput.append(command);
                }
                expressionField.setText(currentInput.toString());
                break;
        }
    }

    private BigDecimal evaluateExpression(String expr) throws Exception {
        expr = expr.trim();

        // Replace √x with sqrt(x)
        StringBuilder parsed = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '%') {
                int j = parsed.length() - 1;
                while (j >= 0 && (Character.isDigit(parsed.charAt(j)) || parsed.charAt(j) == '.')) j--;
                String number = parsed.substring(j + 1);
                BigDecimal percent = new BigDecimal(number).divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
                parsed.replace(j + 1, parsed.length(), percent.toPlainString());
            } else if (c == '√') {
                parsed.append("√");
            } else {
                parsed.append(c);
            }
        }

        expr = parsed.toString();

        while (expr.contains("√")) {
            int idx = expr.lastIndexOf("√");
            int rootCount = 1;
            int i = idx - 1;

            // Count how many consecutive square roots
            while (i >= 0 && expr.charAt(i) == '√') {
                rootCount++;
                idx--;
                i--;
            }

            int start = idx + rootCount;
            int end = start;

            while (end < expr.length() && (Character.isDigit(expr.charAt(end)) || expr.charAt(end) == '.')) {
                end++;
            }

            if (start == end) throw new Exception("Invalid square root");

            String numberStr = expr.substring(start, end);
            double value = Double.parseDouble(numberStr);

            for (int j = 0; j < rootCount; j++) {
                value = Math.sqrt(value);
            }

            expr = expr.substring(0, idx) +
                new BigDecimal(value, MathContext.DECIMAL64).toPlainString() +
                expr.substring(end);
        }

        return evaluateFinal(expr);
    }

    private BigDecimal evaluateFinal(String expr) throws Exception {
        java.util.List<BigDecimal> numbers = new java.util.ArrayList<>();
        java.util.List<Character> operators = new java.util.ArrayList<>();

        StringBuilder num = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if ((Character.isDigit(c) || c == '.')) {
                num.append(c);
            } else if ("+-*/^".indexOf(c) >= 0) {
                if (num.length() == 0 && c == '-') {
                    num.append('-');
                } else {
                    numbers.add(new BigDecimal(num.toString()));
                    num.setLength(0);
                    operators.add(c);
                }
            }
        }
        if (num.length() > 0) numbers.add(new BigDecimal(num.toString()));

        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '^') {
                BigDecimal base = numbers.get(i);
                BigDecimal exponent = numbers.get(i + 1);
                double result = Math.pow(base.doubleValue(), exponent.doubleValue());
                numbers.set(i, new BigDecimal(result, MathContext.DECIMAL64));
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }

        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '*' || operators.get(i) == '/') {
                BigDecimal result = operators.get(i) == '*' ?
                        numbers.get(i).multiply(numbers.get(i + 1)) :
                        numbers.get(i).divide(numbers.get(i + 1), MathContext.DECIMAL128);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }

        for (int i = 0; i < operators.size(); i++) {
            BigDecimal result = operators.get(i) == '+' ?
                    numbers.get(i).add(numbers.get(i + 1)) :
                    numbers.get(i).subtract(numbers.get(i + 1));
            numbers.set(i, result);
            numbers.remove(i + 1);
            operators.remove(i);
            i--;
        }

        return numbers.get(0);
    }

    class RoundedButton extends JButton {
        private boolean hovered = false;
        private boolean pressed = false;
        private final Color baseColor = new Color(142, 180, 123);

        public RoundedButton(String label) {
            super(label);
            setOpaque(false);
            setForeground(new Color(56, 20, 71));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth(), height = getHeight(), arc = Math.round(Math.min(width, height) * 0.15f);
            Color fill = pressed ? baseColor.darker() : (hovered ? baseColor.brighter() : baseColor);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, width, height, arc, arc);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override protected void paintBorder(Graphics g) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorUI().setVisible(true));
    }
}
