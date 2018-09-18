SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[clientes](
	[id] [int] NOT NULL,
	[nome] [varchar](255) NULL
) ON [PRIMARY]

GO
ALTER TABLE [dbo].[clientes] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[person](
	[id] [int] NOT NULL,
	[name] [varchar](255) NULL,
	[phone] [varchar](255) NULL
) ON [PRIMARY]

GO
ALTER TABLE [dbo].[person] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[receita](
	[id] [int] NOT NULL,
	[data] [date] NULL,
	[numero] [varchar](255) NULL,
	[recomendacao] [varchar](255) NULL,
	[idCliente] [int] NULL,
	[idRt] [int] NULL
) ON [PRIMARY]

GO
ALTER TABLE [dbo].[receita] ADD PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[receita]  WITH CHECK ADD  CONSTRAINT [FKl65m8b8pgqx4tjwh1wugqnupu] FOREIGN KEY([idCliente])
REFERENCES [dbo].[clientes] ([id])
GO
ALTER TABLE [dbo].[receita] CHECK CONSTRAINT [FKl65m8b8pgqx4tjwh1wugqnupu]
GO
ALTER TABLE [dbo].[receita]  WITH CHECK ADD  CONSTRAINT [FKypgt3hfpu77tfh8wkqu1n83d] FOREIGN KEY([idRt])
REFERENCES [dbo].[clientes] ([id])
GO
ALTER TABLE [dbo].[receita] CHECK CONSTRAINT [FKypgt3hfpu77tfh8wkqu1n83d]
GO