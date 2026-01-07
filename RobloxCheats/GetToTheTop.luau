-- outdated script.
game.Players.LocalPlayer:Kick("outdated script")

local orion = loadstring(game:HttpGet(('https://raw.githubusercontent.com/shlexware/Orion/main/source')))()
local window = orion:MakeWindow({Name = "Get to the Top UI",HidePremium = false,SaveConfig = false,IntroEnabled = true})
local main = window:MakeTab({Name = "Main",Icon = "",PremiumOnly = false})
local Game = main:AddSection({Name = "Game"})
Game:AddLabel("I know, there are not that much features...")
Game:AddLabel("But like I said, I'll keep on updating this script, whatsoever...")
Game:AddLabel("Auto-farm action will be working on soon...")
Game:AddButton({Name = "Instant Win",Callback = function()
      game.Players.LocalPlayer.Character:WaitForChild("HumanoidRootPart").CFrame = workspace:WaitForChild("TowerSpawn").TowerTop.EndingButton.TipTop.go.CFrame
end})
Game:AddButton({Name = "Instant Win (but 'Smoother')",Callback = function()
      game:GetService("TweenService"):Create(game.Players.LocalPlayer.Character:WaitForChild("HumanoidRootPart"), TweenInfo.new(5, Enum.EasingStyle.Cubic), {CFrame = workspace:WaitForChild("TowerSpawn").TowerTop.EndingButton.TipTop.go.CFrame}):Play()
end})
local Player = main:AddSection({Name = "Client"})
Player:AddButton({Name = "Infinite Yield v5.9",Callback = function()
      loadstring(game:HttpGet('https://raw.githubusercontent.com/EdgeIY/infiniteyield/master/source'))()
end})
local infJump
Player:AddButton({Name = "Infinite Jump",Callback = function()
      game.Players.LocalPlayer:GetMouse().KeyDown:Connect(function(k)
            if k:byte() == 32 then
              game.Players.LocalPlayer.Character.Humanoid:ChangeState("Jumping")
              task.wait()
              game.Players.LocalPlayer.Character.Humanoid:ChangeState("Seated")
            end
        end)
end})
Player:AddToggle({Name = "Swim",Default = false,Callback = function(Bool)
      local swim = nil
      if Bool == true then
        swim = game:GetService("RunService").RenderStepped:Connect(function()
            game.Players.LocalPlayer.Character.Humanoid:ChangeState("Swimming")
        end)
      else
        swim:Disconnect()
      end
end})
ws = 16
Player:AddToggle({Name = "Spoof Walkspeed",Default = false,Callback = function(Bool)
      local wsen = nil
      if Bool == true then
        wsen = game:GetService("RunService").RenderStepped:Connect(function()
            game.Players.LocalPlayer.Character.Humanoid.WalkSpeed = ws
        end)
      else
        wsen:Disconnect()
        game.Players.LocalPlayer.Character.Humanoid.WalkSpeed = 16
      end
end})
Player:AddSlider({Name = "Walkspeed",Min = 16,Max = 100,Default = 16,Increment = 1,Callback = function(Value)
      ws = Value
end})
jp = 50
Player:AddToggle({Name = "Spoof Jumppower",Default = false,Callback = function(Bool)
      local jpen = nil
      if Bool == true then
        jpen = game:GetService("RunService").RenderStepped:Connect(function()
            game.Players.LocalPlayer.Character.Humanoid.JumpPower = jp
        end)
      else
        jpen:Disconnect()
        game.Players.LocalPlayer.Character.Humanoid.JumpPower = 50
      end
end})
Player:AddSlider({Name = "Jumppower",Min = 16,Max = 100,Default = 16,Increment = 1,Callback = function(Value)
      jp = Value
end})

orion:Init()
