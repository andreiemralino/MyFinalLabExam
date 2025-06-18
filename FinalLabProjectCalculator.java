import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FinalLabProjectCalculator extends JFrame implements ActionListener {
    
    private final int BUTTON_SIZE = 60;
    private final int MAX_DIGITS = 30;
    
    private JTextField expressionField;
    private JTextField resultField;
    private StringBuilder currentInput = new StringBuilder();
    private boolean hasDecimal = false;
    private boolean hasExponent = false;
    private boolean hasOperator = false;
    
    private Timer buttonFlashTimer;

    public FinalLabProjectCalculator() {
        setTitle("Calculator");
        int cols = 4;
        int rows = 6;
        int gap = 8;

        int panelWidth = (BUTTON_SIZE + gap) * cols + gap;
        int panelHeight = (BUTTON_SIZE + gap) * rows + gap;
        int displayHeight = 80;

        setSize(panelWidth + 30, panelHeight + displayHeight + 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(188, 214, 180));

        /* ========== DISPLAY AREA ========== */
        JPanel displayPanel = new JPanel(new GridLayout(2, 1));
        displayPanel.setPreferredSize(new Dimension(panelWidth, displayHeight));

        expressionField = new JTextField();
        expressionField.setEditable(false);
        expressionField.setFocusable(false);
        expressionField.setBackground(new Color(172, 199, 157));
        expressionField.setFont(new Font("Arial", Font.PLAIN, 18));
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionField.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 10));

        resultField = new JTextField();
        resultField.setEditable(false);
        resultField.setFocusable(false);
        resultField.setBackground(new Color(172, 199, 157));
        resultField.setFont(new Font("Arial", Font.BOLD, 24));
        resultField.setHorizontalAlignment(SwingConstants.RIGHT);
        resultField.setBorder(BorderFactory.createEmptyBorder(2, 10, 5, 10));

        displayPanel.add(expressionField);
        displayPanel.add(resultField);
        mainPanel.add(displayPanel, BorderLayout.NORTH);

        /* ========== BUTTON PANEL ========== */
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
        buttonPanel.setBackground(new Color(188, 214, 180));

        String[][] layout = {
            {"%", "^", "C", "←"},
            {"√", "/", "*", "-"},
            {"7", "8", "9", "+"},
            {"4", "5", "6", ""},
            {"1", "2", "3", "="},
            {"+/-", "0", ".", ""}
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
                button.addActionListener(this);

                gbc.gridx = col;
                gbc.gridy = row;

                if (text.equals("0") && row == 5 && col == 1) {
                    gbc.gridwidth = 1;
                } else if (text.equals("+") && row == 2) {
                    gbc.gridheight = 2;
                } else if (text.equals("=") && row == 4) {
                    gbc.gridheight = 2;
                } else if (text.equals("+/-") && row == 5 && col == 0) {
                    gbc.gridwidth = 1;
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
        
        buttonFlashTimer = new Timer(150, e -> {
            buttonFlashTimer.stop();
        });
        buttonFlashTimer.setRepeats(false);
    }

    class RoundedButton extends JButton {
        private boolean hovered = false;
        private boolean pressed = false;
        private final Color baseColor = new Color(142, 180, 123);
        private final Color pressedColor = new Color(100, 140, 83);
        private final Color hoverColor = new Color(162, 200, 143);

        public RoundedButton(String label) {
            super(label);
            setOpaque(false);
            setForeground(new Color(56, 20, 71));
            setContentAreaFilled(false);
            
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { 
                    hovered = true; 
                    repaint(); 
                }
                @Override public void mouseExited(MouseEvent e) { 
                    hovered = false; 
                    repaint(); 
                }
                @Override public void mousePressed(MouseEvent e) { 
                    pressed = true; 
                    repaint();
                }
                @Override public void mouseReleased(MouseEvent e) { 
                    pressed = false; 
                    repaint(); 
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth(), height = getHeight();
            int arc = Math.round(Math.min(width, height) * 0.15f);
            
            Color fill;
            if (pressed) {
                fill = pressedColor;
            } else if (hovered) {
                fill = hoverColor;
            } else {
                fill = baseColor;
            }
            
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, width, height, arc, arc);
            
            super.paintComponent(g);
            g2.dispose();
        }

        @Override protected void paintBorder(Graphics g) {}
    }

    private int countDigits(StringBuilder sb) {
        int count = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isDigit(sb.charAt(i))) count++;
        }
        return count;
    }

    private String formatResult(BigDecimal result) {
        if (result.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        boolean isExponentiation = currentInput.toString().contains("^");
        BigDecimal absResult = result.abs();
        
        boolean useScientific = absResult.compareTo(BigDecimal.valueOf(1000000)) >= 0 ||  
                            (absResult.compareTo(BigDecimal.valueOf(0.001)) <= 0 &&    
                            absResult.compareTo(BigDecimal.ZERO) != 0);

        if (isExponentiation) {
            String formatted = result.setScale(12, RoundingMode.HALF_UP)
                                .stripTrailingZeros()
                                .toPlainString();
            if (useScientific) {
                formatted = new DecimalFormat("0.############E0").format(result);
            }
            return formatted;
        }

        if (useScientific) {
            return new DecimalFormat("0.#############E0").format(result);
        }

        if (currentInput.toString().contains("√")) {
            return String.format("%.10f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        
        if (currentInput.toString().contains("/")) {
            return result.setScale(12, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        }
        
        return result.stripTrailingZeros().toPlainString();
    }

    private void adjustResultFontSize() {
        String text = resultField.getText();
        if (text.isEmpty()) return;
        
        Font currentFont = resultField.getFont();
        int stringWidth = resultField.getFontMetrics(currentFont).stringWidth(text);
        int componentWidth = resultField.getWidth() - 20;
        
        if (stringWidth > componentWidth) {
            float newSize = currentFont.getSize2D() * ((float)componentWidth / (float)stringWidth);
            newSize = Math.max(12, newSize);
            resultField.setFont(currentFont.deriveFont(newSize));
        } else {
            resultField.setFont(new Font("Arial", Font.BOLD, 24));
        }
    }

    private BigDecimal power(BigDecimal base, BigDecimal exponent) {
        try {
            if (exponent.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
                if (exponent.compareTo(BigDecimal.ZERO) < 0) {
                    return BigDecimal.ONE.divide(
                        base.pow(-exponent.intValueExact(), MathContext.DECIMAL128),
                        MathContext.DECIMAL128
                    );
                }
                return base.pow(exponent.intValueExact(), MathContext.DECIMAL128);
            }

            MathContext mc = new MathContext(20, RoundingMode.HALF_UP);
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            return new BigDecimal(result).round(mc);

        } catch (ArithmeticException e) {
            double result = Math.pow(base.doubleValue(), exponent.doubleValue());
            return new BigDecimal(result);
        }
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

            case "+/-":
                if (currentInput.length() == 0) {
                    // Don't allow just a negative sign with no number
                    return;
                } else if ("+-*/^%".indexOf(last) >= 0) {
                    // Don't allow negative sign after operator (except for subtraction case)
                    if (last != '-') {
                        return;
                    }
                } else {
                    int numStart = findNumberStart(currentInput.toString(), currentInput.length() - 1);
                    if (numStart >= 0) {
                        String numberStr = currentInput.substring(numStart);
                        // Prevent toggling if we already have a negative sign
                        if (numberStr.startsWith("-") && numberStr.length() > 1) {
                            // Remove the negative sign
                            currentInput.replace(numStart, currentInput.length(), numberStr.substring(1));
                        } else if (!numberStr.startsWith("-") && numberStr.length() > 0) {
                            // Add negative sign only if there's a number
                            currentInput.replace(numStart, currentInput.length(), "-" + numberStr);
                        }
                    }
                }
                expressionField.setText(currentInput.toString());
                break;

            case "=":
                if (currentInput.length() == 0) return;
                try {
                    BigDecimal result = evaluateExpression(currentInput.toString());
                    String displayResult = formatResult(result);
                    resultField.setText(displayResult);
                    expressionField.setText(currentInput.toString());
                    currentInput.setLength(0);
                    currentInput.append(displayResult);
                    adjustResultFontSize();
                } catch (Exception ex) {
                    resultField.setText("Error");
                    currentInput.setLength(0);
                }
                hasDecimal = false;
                hasExponent = false;
                hasOperator = false;
                break;

            case "%":
                if (countDigits(currentInput) >= MAX_DIGITS) return;
                if (Character.isDigit(last) || last == 'E' || last == 'e') {
                    currentInput.append('%');
                    hasOperator = true;
                }
                expressionField.setText(currentInput.toString());
                break;

            case "^":
                if (countDigits(currentInput) >= MAX_DIGITS) return;
                if (!hasExponent && (Character.isDigit(last) || last == 'E' || last == 'e' || last == ')')) {
                    currentInput.append('^');
                    hasExponent = true;
                    hasDecimal = false;
                    hasOperator = false;
                }
                expressionField.setText(currentInput.toString());
                break;

            case ".":
                if (countDigits(currentInput) >= MAX_DIGITS) return;
                if (!hasDecimal) {
                    currentInput.append('.');
                    hasDecimal = true;
                }
                expressionField.setText(currentInput.toString());
                break;

            case "√":
                if (countDigits(currentInput) >= MAX_DIGITS) return;
                if (currentInput.length() > 0) {
                    char lastChar = currentInput.charAt(currentInput.length() - 1);
                    if (Character.isDigit(lastChar) || lastChar == ')' || lastChar == '%' || 
                        lastChar == 'E' || lastChar == 'e') {
                        currentInput.append("*");
                    }
                }
                currentInput.append("√");
                expressionField.setText(currentInput.toString());
                break;

            default:
                if (countDigits(currentInput) >= MAX_DIGITS) return;

                if ("0123456789".contains(command)) {
                    if (last == '%') currentInput.append('*');
                    currentInput.append(command);
                } else if ("+-*/".contains(command)) {
                    if (command.equals("-") && currentInput.length() == 0) {
                        currentInput.append("-");
                        expressionField.setText(currentInput.toString());
                        return;
                    }
                    
                    if ("+*/".contains(command) && currentInput.length() == 0) {
                        return;
                    }

                    if ("+-*/".contains(String.valueOf(last))) return;

                    if (hasOperator) {
                        try {
                            BigDecimal result = evaluateExpression(currentInput.toString());
                            String displayResult = formatResult(result);
                            resultField.setText(displayResult);
                            currentInput.setLength(0);
                            currentInput.append(displayResult);
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
                }
                expressionField.setText(currentInput.toString());
                break;
        }
    }

    private String processSquareRoots(String expr) throws Exception {
        StringBuilder sb = new StringBuilder(expr);
        int sqrtIndex;
        
        while ((sqrtIndex = sb.lastIndexOf("√")) != -1) {
            int numStart = sqrtIndex + 1;
            int numEnd = numStart;
            
            while (numEnd < sb.length() &&
                (Character.isDigit(sb.charAt(numEnd)) || 
                    sb.charAt(numEnd) == '.' || 
                    sb.charAt(numEnd) == 'E' || 
                    sb.charAt(numEnd) == 'e' ||
                    ((sb.charAt(numEnd) == '-' || sb.charAt(numEnd) == '+') && numEnd > 0 &&
                    (sb.charAt(numEnd - 1) == 'E' || sb.charAt(numEnd - 1) == 'e')))) {
                numEnd++;
            }

            if (numStart == numEnd) {
                throw new Exception("Invalid square root");
            }
            
            String numberStr = sb.substring(numStart, numEnd);
            double value = Double.parseDouble(numberStr);
            value = Math.sqrt(value);
            
            sb.replace(sqrtIndex, numEnd, new BigDecimal(value).toPlainString());
        }
        
        return sb.toString();
    }

    private String processPercentages(String expr) throws Exception {
        StringBuilder sb = new StringBuilder(expr);
        int percentIndex;
        
        while ((percentIndex = sb.indexOf("%")) != -1) {
            int numStart = findNumberStart(sb.toString(), percentIndex - 1);
            
            if (numStart == -1 || numStart >= percentIndex) {
                throw new Exception("Invalid percentage expression");
            }
            
            String numberStr = sb.substring(numStart, percentIndex);
            BigDecimal number = parseNumber(numberStr);
            BigDecimal percentValue = number.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
            
            if (numStart > 0) {
                char prevChar = sb.charAt(numStart - 1);
                if ("+-*/".indexOf(prevChar) >= 0) {
                    int opIndex = numStart - 1;
                    int leftNumStart = findNumberStart(sb.toString(), opIndex - 1);
                    
                    if (leftNumStart != -1 && leftNumStart < opIndex) {
                        String leftStr = sb.substring(leftNumStart, opIndex);
                        BigDecimal leftNum = parseNumber(leftStr);
                        BigDecimal result = performOperation(leftNum, percentValue, prevChar);
                        
                        sb.replace(leftNumStart, percentIndex + 1, formatResult(result));
                        continue;
                    }
                }
            }
        
            sb.replace(numStart, percentIndex + 1, formatResult(percentValue));
        }
        
        return sb.toString();
    }

    private BigDecimal parseNumber(String numStr) throws Exception {
        try {
            return new BigDecimal(numStr);
        } catch (NumberFormatException e) {
            try {
                return BigDecimal.valueOf(Double.parseDouble(numStr));
            } catch (NumberFormatException ex) {
                throw new Exception("Invalid number format: " + numStr);
            }
        }
    }

    private BigDecimal performOperation(BigDecimal left, BigDecimal right, char op) throws Exception {
        switch (op) {
            case '+': return left.add(left.multiply(right));
            case '-': return left.subtract(left.multiply(right));
            case '*': return left.multiply(right);
            case '/': return left.divide(right, MathContext.DECIMAL128);
            default: throw new Exception("Invalid operator");
        }
    }

    private int findNumberStart(String expr, int fromIndex) {
        if (fromIndex < 0) return 0;
        
        int i = fromIndex;
        
        while (i >= 0) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.' || c == 'E' || c == 'e') {
                i--;
            } else if (c == '-' && (i == 0 || "+-*/^(".indexOf(expr.charAt(i-1)) >= 0)) {
                i--;
                break;
            } else if ((c == '+' || c == '-') && i > 0 && (expr.charAt(i-1) == 'E' || expr.charAt(i-1) == 'e')) {
                i--;
            } else {
                break;
            }
        }
        
        return i + 1;
    }

    private BigDecimal evaluateFinal(String expr) throws Exception {
        List<BigDecimal> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            
            if (isNumberChar(c, i, expr)) {
                num.append(c);
            } else if ("+-*/^".indexOf(c) >= 0) {
                if (num.length() > 0) {
                    numbers.add(parseNumber(num.toString()));
                    num.setLength(0);
                }
                operators.add(c);
            }
        }
        
        if (num.length() > 0) {
            numbers.add(parseNumber(num.toString()));
        }
        
        // Process exponents first (right to left)
        for (int i = operators.size() - 1; i >= 0; i--) {
            if (operators.get(i) == '^') {
                numbers.set(i, power(numbers.get(i), numbers.get(i + 1)));
                numbers.remove(i + 1);
                operators.remove(i);
            }
        }
        
        // Process multiplication and division
        for (int i = 0; i < operators.size();) {
            char op = operators.get(i);
            if (op == '*' || op == '/') {
                numbers.set(i, performBasicOperation(numbers.get(i), numbers.get(i + 1), op));
                numbers.remove(i + 1);
                operators.remove(i);
            } else {
                i++;
            }
        }
        
        // Process addition and subtraction
        for (int i = 0; i < operators.size();) {
            char op = operators.get(i);
            numbers.set(i, performBasicOperation(numbers.get(i), numbers.get(i + 1), op));
            numbers.remove(i + 1);
            operators.remove(i);
        }
        
        return numbers.isEmpty() ? BigDecimal.ZERO : numbers.get(0);
    }

    private boolean isNumberChar(char c, int pos, String expr) {
    return Character.isDigit(c)
        || c == '.'
        || c == 'E'
        || c == 'e'
        || (c == '-' && (pos == 0 || "+-*/^(".indexOf(expr.charAt(pos - 1)) >= 0))
        || ((c == '+' || c == '-') && pos > 0 && 
            (expr.charAt(pos - 1) == 'E' || expr.charAt(pos - 1) == 'e'));
    }

    private BigDecimal performBasicOperation(BigDecimal a, BigDecimal b, char op) throws Exception {
        switch (op) {
            case '+': return a.add(b, MathContext.DECIMAL128);
            case '-': return a.subtract(b, MathContext.DECIMAL128);
            case '*': return a.multiply(b, MathContext.DECIMAL128);
            case '/': 
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new Exception("Division by zero");
                }
                return a.divide(b, MathContext.DECIMAL128);
            default: throw new Exception("Invalid operator");
        }
    }

    private BigDecimal evaluateExpression(String expr) throws Exception {
        expr = expr.trim();
        expr = processSquareRoots(expr);
        expr = processPercentages(expr);
        return evaluateFinal(expr);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinalLabProjectCalculator calculator = new FinalLabProjectCalculator();
            calculator.setVisible(true);
        });
    }
}
