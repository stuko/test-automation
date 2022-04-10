import discord

client = discord.Client()

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client)) #봇이 실행되면 콘솔창에 표시

@client.event
async def on_message(message):
    if message.author == client.user: # 봇 자신이 보내는 메세지는 무시
        return

    if message.content.startswith('$hello'): # 만약 $hello로 시작하는 채팅이 올라오면
        await message.channel.send('Hello!') # Hello!라고 보내기

client.run('your token here') #토큰