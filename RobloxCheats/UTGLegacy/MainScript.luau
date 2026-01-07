-- Version: 3.1


-- no annoying errors :|
for _,__error in next,getconnections(game:GetService("ScriptContext").Error) do
	__error:Disable()
end

local you = game.Players.LocalPlayer
local lighting = game.Lighting
local char = you.Character
local humanoid = char:FindFirstChildOfClass("Humanoid")

local lib = loadstring(game:HttpGet('https://raw.githubusercontent.com/shlexware/Orion/main/source'))()
local window = lib:MakeWindow({
	Name="Ranxware | UTG V3.1",
	SaveConfig=false,
	IntroEnabled=true,
	IntroText="Ranxware V2"
})

local tag = window:MakeTab({Name="Tag Cheats"})
local movement = window:MakeTab({Name="Movement"})
local experi = window:MakeTab({Name="Experimental"})
local misc = window:MakeTab({Name="Misc"})

local hitboxes = tag:AddSection({Name="Hitboxes"})
local autotag = tag:AddSection({Name="Autotag [BETA]"})
local _humanoid = movement:AddSection({Name="Humanoid"})
local physical = movement:AddSection({Name="Physical"})
local exp_main = experi:AddSection({Name="Main"})
local visuals = experi:AddSection({Name="Visuals [BETA]"})

local hbcon = false
local hbsize = 2
hitboxes:AddToggle({
	Name="Hitbox Extend",
	Default=false,
	Callback=function(value)
		if value then
			hbcon = true
		else
			hbcon = false
		end
	end,
})
hitboxes:AddSlider({
	Name="Hitbox Size",
	Min=2,Max=15,
	Default=2,
	Increment=1,
	ValueName="studs",
	Callback=function(value)
		hbsize = value
	end,
})
local atcon = false
local at_teamcheck = false
local at_runnercheck = true
autotag:AddToggle({
	Name="Autotag",
	Default=false,
	Callback=function(value)
		if value then
			atcon = true
		else
			atcon = false
		end
	end,
})
autotag:AddToggle({
	Name="Team Check",
	Default=false,
	Callback=function(value)
		at_teamcheck = value
	end,
})
autotag:AddToggle({
	Name="Runner/Alive Check",
	Default=true,
	Callback=function(value)
		at_runnercheck = value
	end,
})

local wsval,jpval = 30,32
local wscon,jpcon = false,false
local changeWs,changeJp
_humanoid:AddToggle({
	Name="Walkspeed",
	Default=false,
	Callback=function(value)
		if value then
			wscon = true
			changeWs = function()
				humanoid.WalkSpeed = wsval
			end
			changeWs()
		else
			wscon = false
		end
	end,
})
_humanoid:AddSlider({
	Name="Walkspeed Value",
	Min=26,Max=65,
	Default=30,
	Increment=1,
	Callback=function(value)
		wsval = value
	end,
})
_humanoid:AddToggle({
	Name="Jumppower",
	Default=false,
	Callback=function(value)
		if value then
			jpcon = true
			changeJp = function()
				humanoid.UseJumpPower = true
				humanoid.JumpPower = jpval
			end
			changeJp()
		else
			jpcon = false
		end
	end,
})
_humanoid:AddSlider({
	Name="Jumppower Value",
	Min=30,Max=90,
	Default=32,
	Increment=1,
	Callback=function(value)
		jpval = value
	end,
})
local fclimbcon,noclipcon = false,false
local fmovecon = false
local infj = false
physical:AddToggle({
	Name="Infinite Jump",
	Default=false,
	Callback=function(value)
		if value then
			infj = true
		else
			infj = false
		end
	end,
})
physical:AddToggle({
	Name="Fast Climb",
	Default=false,
	Callback=function(value)
		if value then
			fclimbcon = true
		else
			fclimbcon = false
		end
	end,
})
physical:AddToggle({
	Name="Free Move",
	Default=false,
	Callback=function(value)
		if value then
			fmovecon = true
		else
			fmovecon = false
		end
	end,
})
physical:AddToggle({
	Name="Noclip",
	Default=false,
	Callback=function(value)
		if value then
			noclipcon = true
		else
			noclipcon = false
		end
	end,
})

local atcoin = false
exp_main:AddToggle({
	Name="Autocollect Coins [ALIVE ONLY!]",
	Default=false,
	Callback=function(value)
		if value then
			atcoin = true
		else
			atcoin = false
		end
	end,
})
local esp = false
local boxes = false
local chams = false
local tracers = false
local names = false
local roles = false
local teamcheck = true
local tracerorigin = "Bottom"
local rainbow_ = false
visuals:AddToggle({
	Name="Enable ESP",
	Default=false,
	Callback=function(value)
		if value then
			esp = true
		else
			esp = false
		end
	end,
})
visuals:AddToggle({
	Name="Box ESP",
	Default=false,
	Callback=function(value)
		if value then
			boxes = true
		else
			boxes = false
		end
	end,
})
visuals:AddToggle({
	Name="Cham ESP",
	Default=false,
	Callback=function(value)
		if value then
			chams = true
		else
			chams = false
		end
	end,
})
visuals:AddToggle({
	Name="Tracer ESP",
	Default=false,
	Callback=function(value)
		if value then
			tracers = true
		else
			tracers = false
		end
	end,
})
visuals:AddToggle({
	Name="Name ESP",
	Default=false,
	Callback=function(value)
		if value then
			names = true
		else
			names = false
		end
	end,
})
visuals:AddToggle({
	Name="Role ESP",
	Default=false,
	Callback=function(value)
		if value then
			roles = true
		else
			roles = false
		end
	end,
})
visuals:AddDropdown({
	Name="Tracer Origin",
	Options={"Bottom","Middle","Top","Mouse"},
	Default="Bottom",
	Callback=function(option)
		tracerorigin = option
	end,
})
visuals:AddToggle({
	Name="Team Check",
	Default=true,
	Callback=function(value)
		if value then
			teamcheck = true
		else
			teamcheck = false
		end
	end,
})
visuals:AddToggle({
	Name="Rainbow ESP",
	Default=false,
	Callback=function(value)
		if value then
			rainbow_ = true
		else
			rainbow_ = false
		end
	end
})

local fbcon = false
misc:AddToggle({
	Name="Fullbright",
	Default=false,
	Callback=function(value)
		if value then
			fbcon = true
		else
			fbcon = false
		end
	end,
})
local acccon = false
misc:AddToggle({
	Name="Anti-Color Change",
	Default=false,
	Callback=function(value)
		if value then
			acccon = true
		else
			acccon = false
		end
	end,
})

local function connect(signal, callback)
	local connection = signal:Connect(callback)
	table.insert(lib.Connections,connection)
	return connection
end

local wst__;wst__ = connect(humanoid:GetPropertyChangedSignal("WalkSpeed"),function()
	if wscon then
		changeWs()
	end
end)
local jpt__;jpt__ = connect(humanoid:GetPropertyChangedSignal("JumpPower"),function()
	if jpcon then
		changeJp()
	end
end)
local fct__;fct__ = connect(humanoid.Climbing,function()
	if fclimbcon then
		humanoid.Jump = true
	end
end)
-- connection, in case if you somehow get voided (with noclip/autocollect coins)...
connect(you.CharacterAdded,function(_nchar)
	char = _nchar
	humanoid = char:FindFirstChildOfClass("Humanoid")
	wst__ = connect(humanoid:GetPropertyChangedSignal("WalkSpeed"),function()
		if wscon then
			changeWs()
		end
	end)
	jpt__ = connect(humanoid:GetPropertyChangedSignal("JumpPower"),function()
		if jpcon then
			changeJp()
		end
	end)
	fct__ = connect(humanoid.Climbing,function()
		if fclimbcon then
			humanoid.Jump = true
		end
	end)
end)
connect(game:GetService("RunService").Stepped,function()
	if hbcon then
		for _,i in next,game.Players:GetPlayers() do
			if i ~= you then
				local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
				if root then
					root.Size = Vector3.new(hbsize,hbsize,hbsize)
					root.Transparency = .5
				end
			end
		end
	else
		for _,i in next,game.Players:GetPlayers() do
			if i ~= you then
				local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
				if root then
					root.Size = Vector3.new(2,2,1)
					root.Transparency = 1
				end
			end
		end
	end
	if noclipcon then
		for _,p in next,char:GetDescendants() do
			if p:IsA("BasePart") then
				p.CanCollide = false
			end
		end
	end
	if fbcon then
		lighting.Brightness = 2
		lighting.ClockTime = 14
		lighting.FogEnd = 100000
		lighting.GlobalShadows = false
		lighting.OutdoorAmbient = Color3.fromRGB(128, 128, 128)
		lighting.ExposureCompensation = 0
		for _,atmosphere in next,lighting:GetChildren() do
			if atmosphere:IsA("Atmosphere") then
				atmosphere:Destroy()
			end
		end
	end
	if acccon then
		for _,i in next,lighting:GetChildren() do
			if i:IsA("ColorCorrectionEffect") then
				if i.Name ~= "VHS" and i.Name ~= "Deepfry" then
					i:Destroy()
				end
			end
		end
	end
	if atcoin then
		if you.PlayerRole.Value ~= "Dead" then -- alive check policy
			for _,v in pairs(workspace:GetDescendants()) do
				if v:IsA("MeshPart") and v.MeshId == "rbxassetid://8483581926" then
					humanoid.RootPart.CFrame = v.CFrame
				end
			end
		end
	end
	if fmovecon then
		char.LockMoveVector.Value = false
	end
end)
connect(game:GetService("UserInputService").JumpRequest,function()
	if infj then
		humanoid.RootPart.Velocity = Vector3.new(humanoid.RootPart.Velocity.X,humanoid.JumpPower or humanoid.JumpHeight,humanoid.RootPart.Velocity.Z)
	end
end)
connect(game:GetService("RunService").Heartbeat,function()
	local youroot = humanoid.RootPart
	if atcon then
		for _,i in next,game.Players:GetPlayers() do
			if i ~= you then
				if at_runnercheck then
					if you.PlayerRole.Value ~= "Runner" and you.PlayerRole.Value ~= "Dead" then
						if at_teamcheck then
							if i.PlayerRole.Value ~= you.PlayerRole.Value then
								local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
								if root then
									local distance = (root.Position-youroot.Position).Magnitude
									if distance <= root.Size.Magnitude then
										mouse1click()
									end
								end
							end
						else
							local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
							if root then
								local distance = (root.Position-youroot.Position).Magnitude
								if distance <= root.Size.Magnitude then
									mouse1click()
								end
							end
						end
					end
				else
					if at_teamcheck then
						if i.PlayerRole.Value ~= you.PlayerRole.Value then
							local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
							if root then
								local distance = (root.Position-youroot.Position).Magnitude
								if distance <= root.Size.Magnitude then
									mouse1click()
								end
							end
						end
					else
						local root = i.Character:FindFirstChildOfClass("Humanoid").RootPart
						if root then
							local distance = (root.Position-youroot.Position).Magnitude
							if distance <= root.Size.Magnitude then
								mouse1click()
							end
						end
					end
				end
			end
		end
	end
end)
local function addEsp(target)
	local function addBox()
		local box = Drawing.new("Square")
		box.Color = Color3.new(1,1,1)
		box.Thickness = 0.5
		box.Filled = false
		box.Transparency = 1
		return box
	end
	local function addLine()
		local line = Drawing.new("Line")
		line.Color = Color3.new(1,1,1)
		line.Thickness = 0.5
		return line
	end
	local function addText()
		local text = Drawing.new("Text")
		text.Color = Color3.new(1,1,1)
		text.Size = 11
		text.Center = true
		text.Font = 3
		return text
	end
	local function addCham()
		local cham = Instance.new("BoxHandleAdornment",gethui() or game.CoreGui)
		cham.Color3 = Color3.new(1,1,1)
		cham.Transparency = .5
		cham.AlwaysOnTop = true
		cham.ZIndex = 2
		return cham
	end
	local function alive(target)
		return (target.PlayerRole.Value ~= "Dead")
	end
	local function isSameTeam(target)
		return (target.PlayerRole.Value == you.PlayerRole.Value)
	end
	local box = addBox()
	local tracer = addLine()
	local name = addText()
	local role = addText()
	local text = addText()
	local head = addCham()
	local torso = addCham()
	local larm = addCham()
	local rarm = addCham()
	local lleg = addCham()
	local rleg = addCham()
	connect(game.Players.PlayerRemoving,function(player)
		if player == target then
			box:Destroy()
			tracer:Destroy()
			name:Destroy()
			role:Destroy()
			text:Destroy()
			head:Destroy()
			torso:Destroy()
			larm:Destroy()
			rarm:Destroy()
			lleg:Destroy()
			rleg:Destroy()
		end
	end)
	connect(game:GetService("RunService").Stepped,function()
		if esp then
			if alive(target) then
				local mainpos,vis = workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChildOfClass("Humanoid").RootPart.Position)
				local miscpos1 = workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChild("Head").Position+Vector3.new(0,0.5,0))
				local miscpos2 = workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChildOfClass("Humanoid").RootPart.Position-Vector3.new(0,4,0))
				if teamcheck then
					if isSameTeam(target) or target.PlayerRole.Value == "" then
						box.Visible = false
						tracer.Visible = false
						name.Visible = false
						role.Visible = false
						text.Visible = false
						head.Visible = false
						torso.Visible = false
						larm.Visible = false
						rarm.Visible = false	
						lleg.Visible = false
						rleg.Visible = false
					end
				end
				if rainbow_ then
					box.Color = Color3.fromHSV(tick()%5/5,1,1)
					head.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					torso.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					larm.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					rarm.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					lleg.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					rleg.Color3 = Color3.fromHSV(tick()%5/5,1,1)
					tracer.Color = Color3.fromHSV(tick()%5/5,1,1)
					name.Color = Color3.fromHSV(tick()%5/5,1,1)
					role.Color = Color3.fromHSV(tick()%5/5,1,1)
					text.Color = Color3.fromHSV(tick()%5/5,1,1)
				else
					box.Color = target.Character:FindFirstChild("Torso").Color
					head.Color3 = target.Character:FindFirstChild("Torso").Color
					torso.Color3 = target.Character:FindFirstChild("Torso").Color
					larm.Color3 = target.Character:FindFirstChild("Torso").Color
					rarm.Color3 = target.Character:FindFirstChild("Torso").Color
					lleg.Color3 = target.Character:FindFirstChild("Torso").Color
					rleg.Color3 = target.Character:FindFirstChild("Torso").Color
					tracer.Color = target.Character:FindFirstChild("Torso").Color
					name.Color = target.Character:FindFirstChild("Torso").Color
					role.Color = target.Character:FindFirstChild("Torso").Color
					text.Color = target.Character:FindFirstChild("Torso").Color
				end
				if boxes then
					box.Visible = vis
					box.Size = Vector2.new((2350/mainpos.Z)+2.5,miscpos1.Y-miscpos2.Y)
					box.Position = Vector2.new((mainpos.X-box.Size.X/2)-1,mainpos.Y-box.Size.Y/2)
				else
					box.Visible = false
				end
				if chams then
					head.Visible = true
					torso.Visible = true
					larm.Visible = true
					rarm.Visible = true
					lleg.Visible = true
					rleg.Visible = true
					head.Adornee = target.Character:FindFirstChild("Head")
					torso.Adornee = target.Character:FindFirstChild("Torso")
					larm.Adornee = target.Character:FindFirstChild("Left Arm")
					rarm.Adornee = target.Character:FindFirstChild("Right Arm")
					lleg.Adornee = target.Character:FindFirstChild("Left Leg")
					rleg.Adornee = target.Character:FindFirstChild("Right Leg")
					head.Size = target.Character:FindFirstChild("Head").Size
					torso.Size = target.Character:FindFirstChild("Torso").Size
					larm.Size = target.Character:FindFirstChild("Left Arm").Size
					rarm.Size = target.Character:FindFirstChild("Right Arm").Size
					lleg.Size = target.Character:FindFirstChild("Left Leg").Size
					rleg.Size = target.Character:FindFirstChild("Right Leg").Size
				else
					head.Visible = false
					torso.Visible = false
					larm.Visible = false
					rarm.Visible = false
					lleg.Visible = false
					rleg.Visible = false
				end
				if tracers then
					tracer.Visible = vis
					if tracerorigin == "Top" then
						tracer.To = Vector2.new(workspace.CurrentCamera.ViewportSize.X/2,0)
						tracer.From = Vector2.new(mainpos.X-1,mainpos.Y+(miscpos1.Y-miscpos2.Y)/2)
					elseif tracerorigin == "Middle" then
						tracer.To = workspace.CurrentCamera.ViewportSize/2
						tracer.From = Vector2.new(mainpos.X-1,(mainpos.Y+(miscpos1.Y-miscpos2.Y)/2)-((miscpos1.Y-miscpos2.Y)/2))
					elseif tracerorigin == "Bottom" then
						tracer.From = Vector2.new(mainpos.X-1,mainpos.Y+(miscpos1.Y-miscpos2.Y)/2)
						tracer.To = Vector2.new(workspace.CurrentCamera.ViewportSize.X/2,1000)
					elseif tracerorigin == "Mouse" then
						tracer.To = game:GetService("UserInputService"):GetMouseLocation()
						tracer.From = Vector2.new(mainpos.X-1,(mainpos.Y+(miscpos1.Y-miscpos2.Y)/2)-((miscpos1.Y-miscpos2.Y)/2))
					end
				else
					tracer.Visible = false
				end
				if names then
					name.Visible = vis
					name.Position = Vector2.new(workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChild("Head").Position).X,workspace.CurrentCamera:WorldToViewportPoint(target.Character.Head.Position).Y-20)
					name.Text = target.Name
				else
					name.Visible = false
				end
				if roles then
					role.Visible = vis
					role.Position = Vector2.new(workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChild("Head").Position).X,workspace.CurrentCamera:WorldToViewportPoint(target.Character.Head.Position).Y-20)
					role.Text = target.PlayerRole.Value
				else
					role.Visible = false
				end
				if names and roles then
					name.Visible = false
					role.Visible = false
					text.Visible = vis
					text.Position = Vector2.new(workspace.CurrentCamera:WorldToViewportPoint(target.Character:FindFirstChild("Head").Position).X,workspace.CurrentCamera:WorldToViewportPoint(target.Character.Head.Position).Y-20)
					text.Text = target.Name.." | "..target.PlayerRole.Value
				else
					text.Visible = false
				end
			else
				box.Visible = false
				tracer.Visible = false
				name.Visible = false
				role.Visible = false
				text.Visible = false
				head.Visible = false
				torso.Visible = false
				larm.Visible = false
				rarm.Visible = false	
				lleg.Visible = false
				rleg.Visible = false
			end
		else
			box.Visible = false
			tracer.Visible = false
			name.Visible = false
			role.Visible = false
			text.Visible = false
			head.Visible = false
			torso.Visible = false
			larm.Visible = false
			rarm.Visible = false	
			lleg.Visible = false
			rleg.Visible = false
		end
	end)
end
for _,i in next,game.Players:GetPlayers() do
	if i ~= you then
		addEsp(i)
	end
end
connect(game.Players.PlayerAdded,function(i)
	if i ~= you then
		addEsp(i)
	end
end)

lib:Init()

--[[

~ UPDATE LOGS: ~

* 11/5/22 - First version of UTG script created (the usual stuff...)
* 11/13/22 - Created coin autocollect module for script
* 11/14/22 - Second version released (removed the useless authentication system; fixed bugs; added new features)
* 12/29/22 - 2.5 version released
* 2/14/23 - closed script... (update for v3 soon.)
* 2/22/23 - Third version released (revamped features; new ui library [orion]; added esp [experimental])
* 2/23/23 - [Third version was broken lol] v3.1 (bug fixes)
* 2/24/23 - Visual updates/fixes; movement fixes; free-move
* 3/9/23 - Visual fixes; coin-collect alive check policy; replaced anti-highcontrast w/ anti-colorchange [vhs and deepfry not included]

]]
