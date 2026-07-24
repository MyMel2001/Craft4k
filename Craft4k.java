import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;
import java.awt.Robot;
import java.awt.Point;

public final class Craft4k extends JPanel implements Runnable {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------

    private static final int INTERNAL_WIDTH = 214;
    private static final int INTERNAL_HEIGHT = 120;

    private static final int WORLD_SIZE = 64;
    private static final int WORLD_VOLUME =
            WORLD_SIZE * WORLD_SIZE * WORLD_SIZE;
    private Robot mouseRobot;
    private boolean mouseCaptured = false;
    // ------------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------------

    /*
     * Original:
     * private int[] M = new int[32767];
     *
     * Indexes:
     *   [0] left mouse button
     *   [1] right mouse button
     *   [2] mouse X
     *   [3] mouse Y
     *   keyboard uses key codes
     */
    private final int[] inputState = new int[32767];


    // ------------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------------

    private final BufferedImage framebuffer =
            new BufferedImage(
                    INTERNAL_WIDTH,
                    INTERNAL_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);

    private final int[] pixels =
            ((DataBufferInt) framebuffer
                    .getRaster()
                    .getDataBuffer())
                    .getData();


    private volatile boolean running = true;


    // ------------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------------

    public Craft4k() {

        try {
                mouseRobot = new Robot();
                } catch (Exception ignored) {
        }

        setFocusable(true);

        installKeyboardInput();
        installMouseInput();

        setPreferredSize(
                new Dimension(856, 480));
    }


    private void installKeyboardInput() {

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent event) {

                char key = Character.toLowerCase(event.getKeyChar());

                if (key < inputState.length) {
                    inputState[key] = 1;
                }
            }


            @Override
            public void keyReleased(KeyEvent event) {

                char key = Character.toLowerCase(event.getKeyChar());

                if (key < inputState.length) {
                    inputState[key] = 0;
                }
            }
        });
    }


    private void installMouseInput() {

        addMouseMotionListener(
                new MouseMotionAdapter() {

                    @Override
                    public void mouseMoved(MouseEvent event) {

                        inputState[2] = event.getX();
                        inputState[3] = event.getY();
                    }


                    @Override
                    public void mouseDragged(MouseEvent event) {

                        inputState[2] = event.getX();
                        inputState[3] = event.getY();
                    }
                });


        addMouseListener(
        new MouseAdapter() {

           private void captureMouse() {

            if (!mouseCaptured) {

                mouseCaptured = true;

                setCursor(
                    Toolkit.getDefaultToolkit()
                    .createCustomCursor(
                        new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),
                        new Point(),
                        "")
                    );
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                captureMouse();
            }

            @Override
            public void mousePressed(MouseEvent e) {

                inputState[2] = e.getX();
                inputState[3] = e.getY();

                if (SwingUtilities.isRightMouseButton(e)) {
                    inputState[1] = 1;
                } else {
                    inputState[0] = 1;
                }
            }
            

            @Override
            public void mouseReleased(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {
                    inputState[1] = 0;
                } else {
                    inputState[0] = 0;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

                inputState[2] = 0;
                inputState[3] = 0;
            }
        });
    }


    // ------------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);

        graphics.drawImage(
                framebuffer,
                0,
                0,
                getWidth(),
                getHeight(),
                null);
    }


    // ------------------------------------------------------------------------
    // Game start
    // ------------------------------------------------------------------------

    public void start() {

        Thread.ofPlatform()
                .name("Craft4k")
                .start(this);
    }


    // ------------------------------------------------------------------------
    // Main game loop
    // ------------------------------------------------------------------------

    @Override
    public void run() {

        Random random = new Random();


        // --------------------------------------------------------------------
        // Generate world
        // --------------------------------------------------------------------

        int[] blocks = new int[WORLD_VOLUME];

        random.setSeed(18295169L);


        for (int index = 0; index < WORLD_VOLUME; index++) {

            int height =
                    index / WORLD_SIZE % WORLD_SIZE;

            blocks[index] =
                    height > 32 + random.nextInt(8)
                            ? random.nextInt(8) + 1
                            : 0;
        }


        // --------------------------------------------------------------------
        // Generate textures
        // --------------------------------------------------------------------

        int[] textures =
                new int[16 * 48 * 16];


        for (int blockId = 1; blockId < 16; blockId++) {

            int brightness =
                    255 - random.nextInt(96);


            for (int textureY = 0;
                 textureY < 48;
                 textureY++) {


                for (int textureX = 0;
                     textureX < 16;
                     textureX++) {


                    int color = 0x966C4A;


                    if (blockId == 4) {

                        color = 0x7F7F7F;
                    }


                    if (blockId != 4
                            || random.nextInt(3) == 0) {

                        brightness =
                                255 - random.nextInt(96);
                    }


                    if (blockId == 1) {

                        int grassHeight =
                                ((textureX * textureX * 3
                                        + textureX * 81)
                                        >> 2 & 3) + 18;


                        if (textureY < grassHeight) {

                            color = 0x6AAA40;

                        } else if (textureY < grassHeight + 1) {

                            brightness =
                                    brightness * 2 / 3;
                        }
                    }


                    if (blockId == 7) {

                        color = 0x675231;

                        if (textureX > 0
                                && textureX < 15
                                && ((textureY > 0
                                && textureY < 15)
                                || (textureY > 32
                                && textureY < 47))) {

                            color = 0xBC9862;

                            int edgeX =
                                    Math.abs(textureX - 7);

                            int edgeY =
                                    Math.abs((textureY & 15) - 7);

                            int edge =
                                    Math.max(edgeX, edgeY);


                            brightness =
                                    196
                                    - random.nextInt(32)
                                    + edge % 3 * 32;

                        } else if (random.nextInt(2) == 0) {

                            brightness =
                                    brightness
                                    * (150
                                    - (textureX & 1) * 100)
                                    / 100;
                        }
                    }


                    if (blockId == 5) {

                        color = 0xB53A15;

                        if ((textureX + textureY / 4 * 4) % 8 == 0
                                || textureY % 4 == 0) {

                            color = 0xBCC6E5;
                        }
                    }


                    if (blockId == 8) {

                        color = 0x50D937;

                        if (random.nextInt(2) == 0) {

                            color = 0;
                            brightness = 255;
                        }
                    }


                    if (textureY >= 32) {
                        brightness /= 2;
                    }


                    int red =
                            ((color >> 16) & 255)
                                    * brightness / 255;

                    int green =
                            ((color >> 8) & 255)
                                    * brightness / 255;

                    int blue =
                            (color & 255)
                                    * brightness / 255;


                    textures[
                            textureX
                            + textureY * 16
                            + blockId * 16 * 48
                            ] =
                            red << 16
                            | green << 8
                            | blue;
                }
            }
        }


                // --------------------------------------------------------------------
        // Camera state
        // --------------------------------------------------------------------

        float cameraX = 96.5f;
        float cameraY = 65.0f;
        float cameraZ = 96.5f;

        float velocityX = 0.0f;
        float velocityY = 0.0f;
        float velocityZ = 0.0f;

        float yaw = 0.0f;
        float pitch = 0.0f;


        long lastTickTime =
                System.currentTimeMillis();


        int selectedBlockIndex = -1;
        int selectedFaceOffset = 0;


        // --------------------------------------------------------------------
        // Main loop
        // --------------------------------------------------------------------

        while (running) {

            float sinYaw =
                    (float) Math.sin(yaw);

            float cosYaw =
                    (float) Math.cos(yaw);

            float sinPitch =
                    (float) Math.sin(pitch);

            float cosPitch =
                    (float) Math.cos(pitch);


            while (System.currentTimeMillis()
                    - lastTickTime > 10L) {


                // ------------------------------------------------------------
                // Mouse look
                // ------------------------------------------------------------

                if (inputState[2] > 0) {

                    float mouseX =
                            (inputState[2] - 428)
                                    / 214.0f
                                    * 2.0f;

                    float mouseY =
                            (inputState[3] - 240)
                                    / 120.0f
                                    * 2.0f;


                    float mouseDistance =
                            (float) Math.sqrt(
                                    mouseX * mouseX
                                    + mouseY * mouseY)
                                    - 1.2f;


                    if (mouseDistance < 0) {
                        mouseDistance = 0;
                    }


                    if (mouseDistance > 0) {

                        yaw -=
                                mouseX
                                * mouseDistance
                                / 400.0f;

                        pitch -=
                                mouseY
                                * mouseDistance
                                / 400.0f;


                        if (pitch < -1.57f) {
                            pitch = -1.57f;
                        }


                        if (pitch > 1.57f) {
                            pitch = 1.57f;
                        }
                    }
                }


                lastTickTime += 10L;


                // ------------------------------------------------------------
                // Movement
                // ------------------------------------------------------------

                float forwardMovement = 0.0f;
                float strafeMovement = 0.0f;


                /*
                 * Original:
                 *
                 * this.M[119] - this.M[115]
                 * this.M[100] - this.M[97]
                 *
                 * These correspond to
                 * keypad / arrow-style controls.
                 */

                forwardMovement +=
                        (inputState[100]
                                - inputState[97])
                                * 0.02f;

                strafeMovement +=
                        (inputState[119]
                                - inputState[115])
                                * 0.02f;


                velocityX *= 0.5f;
                velocityZ *= 0.5f;
                velocityY *= 0.99f;


                velocityX +=
                        sinYaw * forwardMovement
                        + cosYaw * strafeMovement;


                velocityZ +=
                        cosYaw * forwardMovement
                        - sinYaw * strafeMovement;


                velocityY += 0.003f;


                // ------------------------------------------------------------
                // Collision movement
                // ------------------------------------------------------------

                movement:
                for (int axis = 0;
                     axis < 3;
                     axis++) {


                    float nextX =
                            cameraX
                            + velocityX
                            * ((axis + 0) % 3 / 2);

                    float nextY =
                            cameraY
                            + velocityY
                            * ((axis + 1) % 3 / 2);

                    float nextZ =
                            cameraZ
                            + velocityZ
                            * ((axis + 2) % 3 / 2);



                    for (int corner = 0;
                         corner < 12;
                         corner++) {


                        int blockX =
                                (int)
                                (nextX
                                + (corner & 1)
                                * 0.6f
                                - 0.3f)
                                - 64;


                        int blockY =
                                (int)
                                (nextY
                                + ((corner >> 2) - 1)
                                * 0.8f
                                + 0.65f)
                                - 64;


                        int blockZ =
                                (int)
                                (nextZ
                                + ((corner >> 1) & 1)
                                * 0.6f
                                - 0.3f)
                                - 64;



                        if (blockX < 0
                                || blockY < 0
                                || blockZ < 0
                                || blockX >= 64
                                || blockY >= 64
                                || blockZ >= 64
                                || blocks[
                                blockX
                                + blockY * 64
                                + blockZ * 4096]
                                > 0) {


                            if (axis == 1) {

                                if (inputState[32] > 0
                                        && velocityY > 0) {

                                    inputState[32] = 0;
                                    velocityY = -0.1f;

                                } else {

                                    velocityY = 0.0f;
                                }
                            }


                            continue movement;
                        }
                    }


                    cameraX = nextX;
                    cameraY = nextY;
                    cameraZ = nextZ;
                }
            }


            // ---------------------------------------------------------------
            // Block editing
            // ---------------------------------------------------------------

            if (inputState[1] > 0
                    && selectedBlockIndex > 0) {

                blocks[selectedBlockIndex] = 0;
                inputState[1] = 0;
            }


            if (inputState[0] > 0
                    && selectedBlockIndex > 0) {

                blocks[
                        selectedBlockIndex
                        + selectedFaceOffset]
                        = 1;

                inputState[0] = 0;
            }


                        // ----------------------------------------------------------------
            // Raycasting
            // ----------------------------------------------------------------

            int hitBlock = 0;
            int hitFace = 0;


            for (int pixelX = 0;
                 pixelX < INTERNAL_WIDTH;
                 pixelX++) {


                float rayScreenX =
                        (pixelX - 107) / 90.0f;


                for (int pixelY = 0;
                     pixelY < INTERNAL_HEIGHT;
                     pixelY++) {


                    float rayScreenY =
                            (pixelY - 60) / 90.0f;


                    float rayLength = 1.0f;


                    float directionX =
                            rayLength * cosPitch
                            + rayScreenY * sinPitch;


                    float directionY =
                            rayScreenY * cosPitch
                            - rayLength * sinPitch;


                    float directionZ =
                            rayScreenX;


                    float rotatedX =
                            directionX * cosYaw
                            + directionZ * sinYaw;


                    float rotatedZ =
                            directionZ * cosYaw
                            - directionX * sinYaw;


                    int pixelColor = 0;
                    int brightness = 255;


                    double closestDistance = 20.0;


                    int textureU = 0;
                    int textureV = 0;


                    for (int axis = 0;
                         axis < 3;
                         axis++) {


                        float rayAxis;


                        if (axis == 0) {

                            rayAxis = rotatedX;

                        } else if (axis == 1) {

                            rayAxis = directionY;

                        } else {

                            rayAxis = rotatedZ;
                        }


                        float inverseAxis =
                                1.0f /
                                (rayAxis < 0
                                        ? -rayAxis
                                        : rayAxis);


                        float stepX =
                                rotatedX
                                * inverseAxis;

                        float stepY =
                                directionY
                                * inverseAxis;

                        float stepZ =
                                rotatedZ
                                * inverseAxis;



                        float fraction =
                                cameraX
                                - (int) cameraX;


                        if (axis == 1) {

                            fraction =
                                    cameraY
                                    - (int) cameraY;
                        }


                        if (axis == 2) {

                            fraction =
                                    cameraZ
                                    - (int) cameraZ;
                        }


                        if (rayAxis > 0) {

                            fraction = 1 - fraction;
                        }


                        float distance =
                                inverseAxis
                                * fraction;


                        float rayX =
                                cameraX
                                + stepX * fraction;

                        float rayY =
                                cameraY
                                + stepY * fraction;

                        float rayZ =
                                cameraZ
                                + stepZ * fraction;



                        if (rayAxis < 0) {

                            if (axis == 0) {
                                rayX--;
                            }

                            if (axis == 1) {
                                rayY--;
                            }

                            if (axis == 2) {
                                rayZ--;
                            }
                        }



                        while (distance < closestDistance) {


                            int worldX =
                                    (int) rayX - 64;

                            int worldY =
                                    (int) rayY - 64;

                            int worldZ =
                                    (int) rayZ - 64;



                            if (worldX < 0
                                    || worldY < 0
                                    || worldZ < 0
                                    || worldX >= 64
                                    || worldY >= 64
                                    || worldZ >= 64) {

                                break;
                            }


                            int blockIndex =
                                    worldX
                                    + worldY * 64
                                    + worldZ * 4096;


                            int blockId =
                                    blocks[blockIndex];


                            if (blockId > 0) {


                                textureU =
                                        ((int)
                                        ((rayX + rayZ)
                                        * 16.0f))
                                        & 15;


                                textureV =
                                        ((int)
                                        (rayY * 16.0f))
                                        & 15;



                                if (axis == 1) {

                                    textureU =
                                            ((int)
                                            (rayX * 16.0f))
                                            & 15;


                                    textureV =
                                            ((int)
                                            (rayZ * 16.0f))
                                            & 15;


                                    if (stepY < 0) {

                                        textureV += 32;
                                    }
                                }


                                int color =
                                        0xFFFFFF;


                                if (blockIndex != selectedBlockIndex
                                        || textureU > 0
                                        && textureV % 16 > 0
                                        && textureU < 15
                                        && textureV % 16 < 15) {


                                    color =
                                            textures[
                                                    textureU
                                                    + textureV * 16
                                                    + blockId * 16 * 48
                                                    ];
                                }


                                if (distance < 5.0f
                                        && pixelX == inputState[2] / 4
                                        && pixelY == inputState[3] / 4) {


                                    hitBlock =
                                            blockIndex;


                                    hitFace = 1;


                                    if (rayAxis > 0) {
                                        hitFace = -1;
                                    }


                                    hitFace <<= axis * 6;
                                }


                                pixelColor = color;


                                brightness =
                                        255
                                        - (int)
                                        (distance
                                        / 20.0f
                                        * 255.0f);


                                brightness =
                                        brightness
                                        * (255
                                        - (axis + 2)
                                        % 3 * 50)
                                        / 255;


                                closestDistance =
                                        distance;
                            }


                            rayX += stepX;
                            rayY += stepY;
                            rayZ += stepZ;

                            distance += inverseAxis;
                        }
                    }


                    int red =
                            ((pixelColor >> 16) & 255)
                                    * brightness / 255;

                    int green =
                            ((pixelColor >> 8) & 255)
                                    * brightness / 255;

                    int blue =
                            (pixelColor & 255)
                                    * brightness / 255;


                    pixels[
                            pixelX
                            + pixelY * INTERNAL_WIDTH]
                            =
                            red << 16
                            | green << 8
                            | blue;
                }
            }


            selectedBlockIndex = hitBlock;
            selectedFaceOffset = hitFace;


            repaint();

            

            try {
                Thread.sleep(2L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}