for /f "delims== tokens=1,2" %%G in (param.txt) do set %%G=%%H
D:
cd D:\Llama\Files
set InputPath=%1
set OutputPath=%2
set name=%3
main.exe -m .\Models\llama2_7b\llama-2-7b-chat.Q6_K.gguf -i --multiline-input --n-gpu-layers 32 -ins --color -c 2048 -p "<<SYS>>%FirstResponse%<</SYS>>" < %InputPath%"\%name%" > %OutputPath%"\%name%"
PAUSE