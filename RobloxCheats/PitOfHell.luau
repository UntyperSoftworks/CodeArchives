
--> Render <--
local renderstepped = game:GetService("RunService").RenderStepped

--> LIB / MAIN <--
local lib = loadstring(game:HttpGet(("https://raw.githubusercontent.com/shlexware/Orion/main/source")))()
local window = lib:MakeWindow({Name = "Pit Of Hell UI", HidePremium = false, SaveConfig = false, IntroEnabled = true})
local main = window:MakeTab({Name = "Main", Icon = "", PremiumOnly = false})
--> Game <--
local Game = main:AddSection({Name = "Game / Client"})
Game:AddButton(
    {
        Name = "Instant Win",
        Callback = function()
            game.Players.LocalPlayer.Character:FindFirstChild("HumanoidRootPart").CFrame =
                game:GetService("Workspace").TowerModulesFolder.HellArea.Finish.CFrame
        end
    }
)
Game:AddButton(
    {
        Name = "Instant Win v2 (REQUIRES FIRETOUCHINTEREST)",
        Callback = function()
            if firetouchinterest then
                firetouchinterest(
                    game.Players.LocalPlayer.Character:FindFirstChild("HumanoidRootPart"),
                    game:GetService("Workspace").TowerModulesFolder.HellArea.Finish,
                    0
                )
            else
                lib:MakeNotification(
                    {
                        Name = "Error!",
                        Content = "Your executor does not support 'firetouchinterest'! Rather do instant win, instead!",
                        Image = "",
                        Time = 10
                    }
                )
            end
        end
    }
)
Game:AddButton(
    {
        Name = "Go To Random Chest",
        Callback = function()
            for _, chest in pairs(workspace.LocalTowerModulesFolder:GetDescendants()) do
                if chest:IsA("Model") then
                    if chest.Name == "CoinChest" then
                        game.Players.LocalPlayer.Character:FindFirstChild("HumanoidRootPart").CFrame =
                            chest.PrimaryPart.CFrame
                    end
                end
            end
        end
    }
)
Game:AddButton(
    {
        Name = "Remove Killbricks (CLIENT)",
        Callback = function()
            for _, p in pairs(workspace.TowerModulesFolder:GetDescendants()) do
                if p:IsA("BasePart") then
                    if p.Material == Enum.Material.Neon then -- gets rid of killbrix
                        p:Destroy()
                    end
                end
            end
        end
    }
)
Game:AddToggle(
    {
        Name = "Feather Falling (REDUCES FALL DAMAGE!)",
        Default = false,
        Callback = function(Bool)
            if Bool == true then
                workspace.Gravity = 10
            elseif Bool == false then
                workspace.Gravity = 196.2
            end
        end
    }
)
--> Player <--
local Plr = main:AddSection({Name = "Player"})
Plr:AddTextbox(
    {
        Name = "Walkspeed",
        Default = "16",
        TextDisappear = false,
        Callback = function(num)
            if num then
                renderstepped:Wait()
                game.Players.LocalPlayer.Character:FindFirstChildOfClass("Humanoid").WalkSpeed = num
            end
        end
    }
)
Plr:AddTextbox(
    {
        Name = "Jump Powar",
        Default = "50",
        TextDisappear = false,
        Callback = function(num)
            if num then
                renderstepped:Wait()
                game.Players.LocalPlayer.Character:FindFirstChildOfClass("Humanoid").JumpPower = num
            end
        end
    }
)

lib:Init()
