// Definición de pines para el joystick y el botón
const int joystickXPin = A0;
const int joystickYPin = A1;
const int joystickButtonPin = 3;

// Variables para almacenar los valores del joystick
int xValue = 512;
int yValue = 512;
int lastButtonState = HIGH;  // Estado previo del botón

void setup() {
  // Inicialización del puerto serie para la comunicación con la computadora
  Serial.begin(9600);
  
  // Configuración del pin del botón como entrada con resistencia pull-up
  pinMode(joystickButtonPin, INPUT_PULLUP);
}

void loop() {
  // Lectura de los valores brutos del joystick y el estado del botón
  int rawXValue = analogRead(joystickXPin);
  int rawYValue = analogRead(joystickYPin);
  int buttonState = digitalRead(joystickButtonPin);

  // Aplicar un filtro para reducir el ruido en los valores del joystick
  xValue = (3 * xValue + rawXValue) / 4;
  yValue = (3 * yValue + rawYValue) / 4;

  // Comprobar si el estado del botón ha cambiado (presionado o liberado)
  if (buttonState != lastButtonState) {
    lastButtonState = buttonState;
    if (buttonState == LOW) {
      // Enviar un mensaje a través del puerto serie cuando el botón se presiona
    Serial.print("X:");
    Serial.print(xValue);
    Serial.print(" Y:");
    Serial.print(yValue);
    Serial.print(" Button:");
    Serial.println(buttonState);
    }
  }

  // Enviar los valores del joystick solo cuando hay un movimiento significativo
  if (abs(rawXValue - xValue) > 10 || abs(rawYValue - yValue) > 10) {
    // Enviar los valores de X y Y del joystick a través del puerto serie
    Serial.print("X:");
    Serial.print(xValue);
    Serial.print(" Y:");
    Serial.print(yValue);
    Serial.print(" Button:");
    Serial.println(buttonState);
  }

  // Añadir un retardo para evitar la saturación del puerto serie
  delay(100);  // Ajustar el tiempo de retardo según sea necesario
}
