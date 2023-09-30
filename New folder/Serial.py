import serial
import pyautogui

# Abre la conexión serial con Arduino (ajusta el puerto COM)
ser = serial.Serial('COM5', 9600)  # Reemplaza 'COMX' con el puerto correcto

while True:
    # Lee los datos enviados por Arduino
    data = ser.readline().decode().strip()
    
    # Divide los datos en partes (X, Y y el estado del botón)
    parts = data.split()
    
    if len(parts) == 3:
        x_value = int(parts[0].split(":")[1])
        y_value = int(parts[1].split(":")[1])
        button_state = int(parts[2].split(":")[1])
        
        # Verifica si el botón del joystick está presionado
        if button_state == 1:
            # Invierte la dirección vertical del cursor y ajusta los valores para controlar el cursor de manera más suave
            x_value = (x_value - 512) // 10
            y_value = (y_value - 512) // 10
            y_value = -y_value  # Invertir la dirección vertical
            
            # Mueve el cursor en función de los valores del joystick
            current_x, current_y = pyautogui.position()
            new_x = current_x + x_value
            new_y = current_y + y_value
            
            # Limitar el movimiento dentro de la pantalla
            screen_width, screen_height = pyautogui.size()
            new_x = max(0, min(new_x, screen_width - 5))
            new_y = max(0, min(new_y, screen_height - 5))
            
            pyautogui.moveTo(new_x, new_y)
        
        elif button_state == 0:  # Si el botón del joystick está presionado (cambiado a 0)
            pyautogui.click()  # Realiza un clic del mouse
        
ser.close()
